import java.util.ArrayList;

import java.time.LocalTime;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpreadSheetToScanData {
	
	public static ArrayList<CardScanData> ScanDataFromSheet(String sheetName) {
		ArrayList<CardScanData> cardScanDataList = new ArrayList<CardScanData>();
		
		Path path = Paths.get(sheetName);
		
		InputStreamReader inputStreamRead = null;

		if(Files.exists(path)) {
			//System.out.println("Looking in file");

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
				if(CsvHandler.StepToCommaStatic(data).equals("ID")) {
					continue;
				}
				cardScanDataList.add(CardScanFromLine(data));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		} else {
			System.out.println("ScanData not loaded from sheet; No file was found");
			return null;
		}

		return cardScanDataList;
	}
	
	static CardScanData CardScanFromLine(String line) {
		CsvHandler csvHandle = new CsvHandler(line);
		
		int Id = Integer.parseInt(csvHandle.StepToComma());
		
		String name = csvHandle.StepToComma();
		
		StudentData student = new StudentData(Id, name);
		
		Boolean tardy = Boolean.parseBoolean(csvHandle.StepToComma());
		
		Boolean suspicious = Boolean.parseBoolean(csvHandle.StepToComma());
		
		ArrayList<LocalTime> times = new ArrayList<LocalTime>();
		
		String data;
		while ((data = csvHandle.StepToComma()) != null) {
			if(data.equals(" ")) {
				continue;
			}
			try {
				LocalTime time = LocalTime.parse(data);
				times.add(time);
			} catch (Exception e) {
				System.out.println("Failed to add (" + data + ") as a time");
				e.printStackTrace();
			}
		}
		
		return new CardScanData(student, tardy, suspicious, times);
	}

}
