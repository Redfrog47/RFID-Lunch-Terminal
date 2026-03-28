import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class DataKey {
	StudentData[] studentDataArr;
	int numStudents;

	public DataKey() {
		studentDataArr = null;
	}

	public DataKey(int _numStudents) {

		numStudents = _numStudents;
		studentDataArr = new StudentData[numStudents];
	}

	public DataKey(int _numStudents, StudentData[] _studentDataArr) {

		numStudents = _numStudents;
		studentDataArr = _studentDataArr;
	}

	public DataKey(StudentData[] _studentDataArr) {
		studentDataArr = _studentDataArr;
		numStudents = _studentDataArr.length;
	}

	public void DataSetup() {
		Scanner scanner = new Scanner(System.in);

		
		for (int i = 0; i < numStudents; i++) { 
			DataEntry(i, scanner);
		}
		  
		PrintStudentData();
		 

		scanner.close();
	}

	void DataEntry(int i, Scanner scanner) {
		System.out.println("Enter student ID");

		String idInput = scanner.next();
		int id;

		try {
			id = Integer.parseInt(idInput);
			System.out.println("");
		} catch (NumberFormatException e) {
			id = -1;
			System.out.println("Invalid ID entered");
			System.out.println("");
			DataEntry(i, scanner);
			return;
		}

		System.out.println("Enter students first name");
		String nameInput = scanner.next();

		System.out.println("Enter students last name");
		nameInput += " " + scanner.next();

		System.out.println("");
		System.out.println("Adding student with ID: " + id + ", and Name: " + nameInput);
		System.out.println("");

		studentDataArr[i] = new StudentData(id, nameInput);
	}

	void PrintStudentData() {
		System.out.println("Printing student list");

		for (int i = 0; i < numStudents; i++) {
			StudentData stu = studentDataArr[i];
			System.out.println("ID: " + stu.id + ", Name: " + stu.name);
		}

		System.out.println("");

	}

	public void SaveKeyToFile(String filePath) {
		Path path = Paths.get(filePath);
		
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add(Integer.toString(numStudents));
		
		for(int i = 0; i < numStudents; i++) {
			StudentData stuData = studentDataArr[i];
			String line = Integer.toString(stuData.id) + " " + stuData.name;
			
			lines.add(line);
		}
		
		try {
		    Files.write(path, lines);
		} catch (IOException e) {
		    e.printStackTrace();
		}
        
	}

	public void SaveDataKey() {
		Path path = Paths.get(GlobalFilepaths.globalKeyPath);

		ArrayList<String> lines = new ArrayList<String>();

		for(int i = 0; i < studentDataArr.length; i++) {
			String id = Integer.toString(studentDataArr[i].id);
			String name = studentDataArr[i].name;

			lines.add(id + "," + name + "\n");
		}

		try {
			Files.write(path, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void UpdateDataKey() {
		studentDataArr = StudentDataArrFromCsvFile();
	}
	
	public static void CreateDataKeyFile(String filePath) {
		String data = "";

		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter number of students");
		
		int studentCount = 0;
		String stuInput = scanner.next();
		
		try {
			studentCount = Integer.parseInt(stuInput);
		} catch (NumberFormatException e) {
			System.out.println("Incorrect count entered");
 			studentCount = -1;
			scanner.close();
			return;
		}

		data += Integer.toString(studentCount) + "\n";

		for(int i = 0; i < studentCount; i++) {
			System.out.println("Enter student id");
			String id = scanner.next();
			System.out.println();
			

			System.out.println("Enter student first name");
			String fName = scanner.next();
			System.out.println();
			
			System.out.println("Enter student last name");
			String lName = scanner.next();
			System.out.println();

			data += id + " " + fName + " " + lName + "\n";
		}

		Path path = Paths.get(filePath + ".txt");

		try {
			Files.write(path, data.getBytes());
			System.out.println("Data key created");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error writing data key");
			scanner.close();
		}


		scanner.close();
	} 

	/// Returns the line number of the appended line
	public static int AppendToKey(String line) {
		Path path = Paths.get(GlobalFilepaths.globalKeyPath );

		line += "\n";

		try {
			Files.write(path, line.getBytes(), StandardOpenOption.APPEND);
		} catch (NoSuchFileException e) {
			try {
				Files.write(path, line.getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (IOException e) {
		 	e.printStackTrace();
			System.out.println("Error appending data key");
		}

		return CsvLineCount(GlobalFilepaths.globalKeyPath);
	}

	public static int CsvLineCount(String filepath) {
		Path path = Paths.get(filepath);
		int lineCount = -1;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(path)));
			String data;
			String line = "";
			while ((data = in.readLine()) != null) {
				line = data;
				if(StudentDataFromLine(data) != null) {
					lineCount++;
				}
			}

			//Check if last line was null to return invalid count
			if(StudentDataFromLine(line) == null) {
				lineCount = -1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lineCount;
	}

	public static void ClearDataKey() {
		Path path = Paths.get(GlobalFilepaths.globalKeyPath);

		try {
			Files.write(path, "".getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void CreateDataKeyFileFromString(String dataKey) {
		Path path = Paths.get(GlobalFilepaths.globalKeyPath);

		try {
			Files.write(path, dataKey.getBytes());
			System.out.println("Data key created");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error writing data key");
		}
	}

	public static DataKey DataKeyFromCsvFile() {
		ArrayList<StudentData> studentList = new ArrayList<StudentData>();

		Path path = Paths.get(GlobalFilepaths.globalKeyPath);
		
		InputStreamReader inputStreamRead = null;

		if(Files.exists(path)) {
			try {
				inputStreamRead = new InputStreamReader(Files.newInputStream(path));
			} catch (IOException e) {
				System.out.println("Could not init InputStreamReader");
			e.printStackTrace();
			}
			BufferedReader in = new BufferedReader(inputStreamRead);
		
			String data = "";
			try {
				while((data = in.readLine()) != null) {
					StudentData studentData = StudentDataFromLine(data);
					if(studentData != null) {
						studentList.add(studentData);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No file to init DataKey");
			return new DataKey();
		}

		return new DataKey(StudentListToArray(studentList));
	}

	public static StudentData StudentDataFromLine(String line) {
		CsvHandler csvHandle = new CsvHandler(line);

		int Id = -1;
		
		try {
			Id = Integer.parseInt(csvHandle.StepToComma());
		} catch (NumberFormatException e) {
			//System.out.println("Invalid ID stored in key");
			return null;
		}
		
		String name = csvHandle.StepToComma();

		return new StudentData(Id, name);
	}

	public static StudentData[] StudentListToArray(ArrayList<StudentData> studentList) {
		int size = studentList.size();
		StudentData[] studentArr = new StudentData[size];

		for(int i = 0; i < size; i++) {
			studentArr[i] = studentList.get(i);
		}

		return studentArr;
	}

	public static StudentData[] StudentDataArrFromCsvFile() {
		ArrayList<StudentData> studentList = new ArrayList<StudentData>();

		Path path = Paths.get(GlobalFilepaths.globalKeyPath);
		
		InputStreamReader inputStreamRead = null;

		if(Files.exists(path)) {
			try {
				inputStreamRead = new InputStreamReader(Files.newInputStream(path));
			} catch (IOException e) {
				System.out.println("Could not init InputStreamReader");
				e.printStackTrace();
			}
			BufferedReader in = new BufferedReader(inputStreamRead);
		
			String data = "";
				try {
				while((data = in.readLine()) != null) {
					studentList.add(StudentDataFromLine(data));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("File not found");
			return null;
		}

		return StudentListToArray(studentList);
	}
}
