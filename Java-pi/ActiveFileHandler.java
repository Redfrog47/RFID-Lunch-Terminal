import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class ActiveFileHandler {

    SocketHandler socketHandler;

    public ActiveFileHandler(SocketHandler _socketHandler) {
        socketHandler = _socketHandler;
    }

    public static String GetDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        ZoneId zone = ZoneId.of("America/New_York");
        LocalDate date = LocalDate.now(zone);
        
        return "Spreadsheets/" + date.format(formatter) + ".csv";
    }

    public void UpdateDate() throws InterruptedException {
        while (true) {
            String date = GetDate();

            if(CheckIfNewDate(date)) {
                GlobalFilepaths.SetGlobalSheetPath(date);

                socketHandler.scanHandler.ClearScanHandler();
            }

            Thread.sleep(60000);
        }
    }

    public boolean CheckIfNewDate(String date) {
        return !date.equals(GlobalFilepaths.globalSheetPath);
    }
}
