import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class DateTimeHandler {
    /// Takes date and time formatted as "yyyy-MM-dd HH:mm:ss"
    public static void SetSystemTime(String formattedDateTime) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "date", "-s", formattedDateTime
        );

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if(exitCode != 0) {
            throw new RuntimeException("Failed to set date and time to: " + formattedDateTime);
        }

        System.out.println("Set date and time to: " + formattedDateTime);
    }

    
    ///@ Assumes realTimeFromWindows is in the format: yyyy-MM-dd HH:mm:ss
    ///@ Returns the time drift in minutes
    public static int FindTimeDriftAndResetSystemTime(String realTimeFromWindows) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalTime realLocalTime = LocalDateTime.parse(realTimeFromWindows, formatter).toLocalTime();

        System.out.println("Comparing: " + LocalTime.now().toString() + " with " + realLocalTime.toString());
        
        int timeDrift = (int)Duration.between(LocalTime.now(), realLocalTime).toMinutes();

        SetSystemTime(realTimeFromWindows);

        ActiveFileHandler.GetDate();

        System.out.println("Time drift: " + timeDrift);

        return timeDrift;
    }
}
