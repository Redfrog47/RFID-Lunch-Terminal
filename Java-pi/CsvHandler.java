import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvHandler {
	String line;
	
	public CsvHandler(String _line) {
		line = _line;
	}
	
	public String StepToComma() {
		String output = "";
		
		if(line == null) {
			return null;
		}
		
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				line = line.substring(i + 1, line.length());
				break;
			} else if(i == line.length() - 1) {
				output += line.charAt(i);
				line = null;
				break;
			}
			else {
				output += line.charAt(i);
			}
		}
		return output;
	}
	
	public static String StepToCommaStatic(String line) {
		String output = "";
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == ',') {
				break;
			}
			else {
				output += line.charAt(i);
			}
		}
		return output;
	}

	public static void ClearAllCsvs() {
		Path startPath = Paths.get("Spreadsheets/"); 

        try (Stream<Path> paths = Files.walk(startPath)) {
            List<String> filePaths = paths
                .filter(Files::isRegularFile) 
                .map(Path::toString)
                .collect(Collectors.toList());

            for(int i = 0; i < filePaths.size(); i++) {
				File csv = new File(filePaths.get(i));
				csv.delete();
			}
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public static List<String> GetAllCsvFiles() {
		List<String> filePaths = null;

		Path startPath = Paths.get("Spreadsheets/"); 

        try (Stream<Path> paths = Files.walk(startPath)) {
            filePaths = paths
                .filter(Files::isRegularFile) 
                .map(Path::toString)
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

		return filePaths;
	}

	
}
