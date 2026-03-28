import java.util.ArrayList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScanDataToSpreadSheet {
	
	public static void CreateSpreadSheet(ArrayList<CardScanData> cardsScanned, String fileName) {
		Path path = Paths.get(fileName);
		
		String data = CompileLines(cardsScanned);
		
		try {
		    Files.write(path, data.getBytes());
		    //System.out.println("Spreadsheet written");
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("Error saving spreadsheet");
		}
	}
	
	public static void ReadSpreadSheet(String fileName) {
		Path path = Paths.get(fileName);
		
		try {
			String data = Files.readString(path);
			System.out.println(data);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading spreadsheet");
		}
	}
	
	static String CreateLine(CardScanData scanData, int maxScans) {
		String output = "";
		
		StudentData student = scanData.student;
		
		output += student.id + "," + student.name + ",";
		
		output += Boolean.toString(scanData.tardy) + ",";
		
		output += Boolean.toString(scanData.suspicious) + ",";
		
		if(scanData.times.size() == maxScans) {
		
			for(int i = 0; i < scanData.times.size(); i++) {
				if(i != scanData.times.size() - 1) {
				output += scanData.times.get(i).toString() + ",";
					}
				else if (i == scanData.times.size() - 1) {
					output += scanData.times.get(i).toString() + "\n";
				}
			}
		} else {
			int leftOverCells = maxScans - scanData.times.size();

			for(int i = 0; i < scanData.times.size(); i++) {
			
				output += scanData.times.get(i).toString() + ",";
			}

			for(int i = 0; i < leftOverCells - 1; i++) {
				output += " ,";
			}

			output += " \n";
		}
		
		
		
		return output;
	}
	
	static String CompileLines(ArrayList<CardScanData> cardsScanned) {
		String output = "ID,Name,Tardy,Suspicious,";
		
		int timeLines = MaxScans(cardsScanned);
		
		for(int i = 1; i < timeLines; i++) {
			output += "Scan " + Integer.toString(i) + ",";
		}
		
		output += "Scan " + Integer.toString(timeLines) + "\n";
		
		for(int i = 0; i < cardsScanned.size(); i++) {
			output += CreateLine(cardsScanned.get(i), timeLines);
		}
		
		return output;
	}
	
	static int MaxScans(ArrayList<CardScanData> cardsScanned) {
		int output = 0;
		
		for(int i = 0; i < cardsScanned.size(); i++) {
			if(cardsScanned.get(i).times.size() > output) {
				output = cardsScanned.get(i).times.size();
			}
		}
		
		return output;
	}
}
