import java.util.ArrayList;
import java.time.LocalTime;


public class CardScanData {
	StudentData student;
	boolean tardy;
	boolean suspicious;
	ArrayList<LocalTime> times;
	
	public CardScanData(StudentData _student, LocalTime firstScan) {
		student = _student;
		times = new ArrayList<LocalTime>();
		times.add(LocalTime.of(firstScan.getHour(), firstScan.getMinute()));
		
		tardy = false;
		suspicious = false;
	}
	
	public CardScanData(StudentData _student, boolean _tardy, boolean _suspicious, ArrayList<LocalTime> _times) {
		student = _student;
		tardy = _tardy;
		suspicious = _suspicious;
		times = _times;
	}
	
	public void OnScan(LocalTime time) {
		time = LocalTime.of(time.getHour(), time.getMinute());
		times.add(time);
	}
	
	public void FormatToSave() {
		if(times.size() % 2 == 0) {
			suspicious = false;
		}
		else {
			suspicious = true;
		}
		
		for(int i = 0; i < times.size(); i++) {
			if(times.get(i).isAfter(TimeFile.bellTime)) {
				tardy = true;
			}
		}
	}

	public void FixTimes(int timeDrift, SocketHandler socketHandler) {
		System.out.println("Fixing times for student: " + student.id);
		for (int i = 0; i < times.size(); i++) {
			
			if(times.get(i).isAfter(TimeFile.lastSyncTime)) {
				System.out.println(times.get(i).toString() + " -> " + times.get(i).plusMinutes(timeDrift));
				LocalTime time = times.get(i).plusMinutes(timeDrift);

				socketHandler.GetLatestSyncedTime(time);

				times.set(i, time);
			} else {
				System.out.println(times.get(i).toString() + " is before last synced time");
			}
			
		}
		System.out.println();
	}
}
