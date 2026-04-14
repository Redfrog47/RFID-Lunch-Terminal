import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class DateTimeHandler {
    /// Takes date and time formatted as "yyyy-MM-dd HH:mm:ss"
    public static void SetSystemTime(String formattedDateTime) throws Exception {
        ProcessBuilder dateProcessBuilder = new ProcessBuilder(
            "date", "-s", formattedDateTime
        );

        Process dateProcess = dateProcessBuilder.start();

        int dateExitCode = dateProcess.waitFor();

        if(dateExitCode != 0) {
            throw new RuntimeException("Failed to set date and time to: " + formattedDateTime);
        } else {
            System.out.println("Set system date and time to: " + formattedDateTime);
        }

        ProcessBuilder hwclockProcessBuilder = new ProcessBuilder(
            "hwclock", "-w"
        );

        Process hwclockProcess = hwclockProcessBuilder.start();

        int hwclockExitCode = hwclockProcess.waitFor();

        if( hwclockExitCode != 0) {
            throw new RuntimeException("Failed to update RTC");
        } else {
            System.out.println("Wrote to RTC");
        }
    }

    
    ///@ Assumes realTimeFromWindows is in the format: yyyy-MM-dd HH:mm:ss
    public static void FindTimeDriftAndResetSystemTime(String realTimeFromWindows) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalTime realLocalTime = LocalDateTime.parse(realTimeFromWindows, formatter).toLocalTime();

        int timeDrift = (int)Duration.between(LocalTime.now(), realLocalTime).toMinutes();

        if(timeDrift > 1) {
            SetSystemTime(realTimeFromWindows);
        }
        
        System.out.println("Time drift: " + timeDrift);
    }
}
