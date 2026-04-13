import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeFile {
    public static LocalTime bellTime;
    public static LocalTime lastSyncTime;

    public static LocalTime TimeFromFile(String filePath) {
        String timeString = "";

        Path path = Paths.get(filePath);

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

    public static void SaveTimeToFile(LocalTime time, String filePath) {
        String timeString = time.toString();

        Path path = Paths.get(filePath);

        try {
            Files.write(path, timeString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void UpdateBellFile(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeString, formatter);

            SaveTimeToFile(time, GlobalFilepaths.globalBellPath);
            bellTime = time;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
    }

    public static void LoadBellTime() {
        bellTime = TimeFromFile(GlobalFilepaths.globalBellPath);
    }

    public static void UpdateSyncFile(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(timeString, formatter);

            SaveTimeToFile(time, GlobalFilepaths.globalSyncPath);
            lastSyncTime = time;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
    }

    public static void LoadSyncTime() {
        lastSyncTime = TimeFromFile(GlobalFilepaths.globalSyncPath);
    }
}
