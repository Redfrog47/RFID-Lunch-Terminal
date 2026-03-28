import java.util.Scanner;

public class KeyMaker {
    public static String CreateDataKeyString() {
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
			return null;
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

		scanner.close();

        return data;
	}
}
