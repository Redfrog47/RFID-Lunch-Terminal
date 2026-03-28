import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SheetHandler {
	public static void ClearSheet(String sheetName) {
		Path path = Paths.get(sheetName);
		
		try {
			Files.write(path, "".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
