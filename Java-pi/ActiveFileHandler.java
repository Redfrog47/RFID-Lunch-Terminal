import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;

public class ActiveFileHandler {

    SocketHandler socketHandler;

    public ActiveFileHandler(SocketHandler _socketHandler) {
        socketHandler = _socketHandler;
    }

    public static String GetDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        LocalDate date = LocalDate.now();
        
        return "Spreadsheets/" + date.format(formatter) + ".csv";
    }

    public void UpdateDate() throws InterruptedException {
        while (true) {
            ResetOnNewDate();

            Thread.sleep(60000);
        }
    }

    public boolean ResetOnNewDate() {
        String date = GetDate();

        if(CheckIfNewDate(date)) {
            GlobalFilepaths.SetGlobalSheetPath(date);

            socketHandler.scanHandler.ClearScanHandler();

            TimeFile.UpdateSyncFile(LocalTime.MIDNIGHT);

            return true;
        }

        return false;
    }


    public boolean CheckIfNewDate(String date) {
        return !date.equals(GlobalFilepaths.globalSheetPath);
    }
}
