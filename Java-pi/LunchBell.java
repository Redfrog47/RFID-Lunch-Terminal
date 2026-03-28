import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LunchBell {
    public static LocalTime bellTime;

    public static LocalTime BellTimeFromFile() {
        String timeString = "";

        Path path = Paths.get(GlobalFilepaths.globalBellPath);

        try {
            InputStreamReader streamReader = new InputStreamReader(Files.newInputStream(path));

            BufferedReader in = new BufferedReader(streamReader);

            String data = "";

            while ((data = in.readLine()) != null) {
                timeString += data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeString, formatter);

            System.out.println("Setting bell time to: " + time.toString());

            return time;
        } catch (DateTimeParseException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static void SaveBellTimeToFile(LocalTime time) {
        String timeString = time.toString();

        Path path = Paths.get(GlobalFilepaths.globalBellPath);

        try {
            Files.write(path, timeString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateBellTime(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeString, formatter);

            SaveBellTimeToFile(time);
            bellTime = time;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
    }

    public static void LoadBellTime() {
        bellTime = BellTimeFromFile();
    }
}
