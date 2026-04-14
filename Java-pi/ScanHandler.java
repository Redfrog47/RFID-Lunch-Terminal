import java.util.ArrayList;
import java.time.LocalTime;


public class ScanHandler {
	ArrayList<CardScanData> cardsScanned;
	DataKey key;
	
	public ScanHandler() {
		cardsScanned = new ArrayList<CardScanData>();
		
		key = DataKey.DataKeyFromCsvFile();
	}
	
	public ScanHandler(String sheetName) {
		cardsScanned = SpreadSheetToScanData.ScanDataFromSheet(sheetName);

		if(cardsScanned == null) {
			cardsScanned = new ArrayList<CardScanData>();
		}
		
		key = DataKey.DataKeyFromCsvFile();
	}
	
	public void OnCardScanned(int cardIndex) {
		CardScanData scan = CardAlreadyScanned(cardIndex);
		
		if(scan != null) {
			scan.OnScan((LocalTime.now()));
			//System.out.println("Card with index " + cardIndex + " has been rescanned");
		} else {
		StudentData studentData = null;
		if(key.studentDataArr[cardIndex] != null) {
			studentData = key.studentDataArr[cardIndex];
		}
		else {
			System.out.println("No student data at card index");
			System.out.println();
			return;
		}
		scan = new CardScanData(studentData, LocalTime.now());
		//System.out.println("Card with index " + cardIndex + " has been scanned");
		cardsScanned.add(scan);
		}
	}

	public void ScanStudentById(int id) {
		for(int i = 0; i < key.studentDataArr.length; i++) {
			if(id == key.studentDataArr[i].id) {
				//System.out.println("Scanning index " + i);
				OnCardScanned(i);
				return;
			}
		}

		System.out.println("No student at index: " + id);
	}
	
	CardScanData CardAlreadyScanned(int cardIndex) {
		if(cardsScanned == null) {
			return null;
		}

		for(int i = 0; i < cardsScanned.size(); i++) {
			if(cardsScanned.get(i).student.id == key.studentDataArr[cardIndex].id) {
				return cardsScanned.get(i);
			}
		}
		
		return null;
	}
	
	public void FormatScans() {
		for(int i = 0; i < cardsScanned.size(); i++) {
			cardsScanned.get(i).FormatToSave();
		}
	}
	
	public void SaveToSheet(String sheetName) {
		FormatScans();
		ScanDataToSpreadSheet.CreateSpreadSheet(cardsScanned, sheetName);
	}
	
	public void ClearScanHandler() {
		if(cardsScanned != null) {
			cardsScanned.clear();
		}
	}
	
	public int maxIndex() {
		return key.studentDataArr.length - 1;
	}

	public void UpdateKey() {
		key.UpdateDataKey();
	}

	public void FixCardScanTimes(int timeDrift) {
		for(int i = 0; i < cardsScanned.size(); i++) {
			cardsScanned.get(i).FixTimes(timeDrift);
		}

		SaveToSheet(GlobalFilepaths.globalSheetPath);
	}
}
