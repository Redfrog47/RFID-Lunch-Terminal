import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class KeySocket {
    int port;
    String ip;
	
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;

	private GuiHandler guiHandler;

	String currentBellTime;

    public KeySocket(int _port, String _ip){
        port = _port;
        ip = _ip;
    }

	public void SetGuiHandler(GuiHandler _guiHandler) {
		guiHandler = _guiHandler;
	}

    void ConnectSocket() {
		try {
			//System.out.println("Creating key socket");
			socket = new Socket(ip, port);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			RequestCurrentBellTime();

			HandleSocket();
		} catch (UnknownHostException e) {
			System.out.println("Unkown host");
			e.printStackTrace();
		} catch (ConnectException e) {
			RetryConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void RetryConnection() {
		try {
			Thread.sleep(1000);
			ConnectSocket();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    void HandleSocket() {
        try {
			String data;
			while((data = in.readLine()) != null) {
				System.out.println("Key socket received: " + data);
				if(data.length() > 7 && data.substring(0, 8).equals("ISuccess")) {
					guiHandler.OnScanConfirmed();
				}
				if(data.length() > 5 && data.substring(0,6).equals("IError")) {
					guiHandler.OnBadScan();
				}
				if(data.length() > 4 && data.substring(0, 4).equals("Time")) {
					String timeString = data.substring(4);
					SetNewBellTime(timeString);
				}
			}
		} catch (SocketException e) {
			RetryConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	void AppendLineToKey(String line) {
		line = "Append" + line;

		System.out.println("Sending: '" + line + "'");
		out.println(line);
	}

	void ClearForNewYear() {
		String data = "Clear";

		out.println(data);
	}

	void ClearKey() {
		String data = "CKey";

		out.println(data);
	}

	void ClearSheet() {
		String data = "CSheet";

		out.println(data);
	}

	void UpdateBellTime(String timeString) {
		out.println("Bell" + timeString);
	}

	void RequestCurrentBellTime() {
		out.println("Time");
	}

	String CurrentBellTime() {
		return currentBellTime;
	}

	void SetNewBellTime(String timeString) {
		currentBellTime = timeString;
	}

	void ShutdownPi() {
		out.println("Shutdown");
	}
}
