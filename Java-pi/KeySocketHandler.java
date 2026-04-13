import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
public class KeySocketHandler {
    int port;
    String ip;

	private SocketHandler sockHandle;

	private ArrayList<Socket> sockList;

    public KeySocketHandler(int _port) {
        port = _port;

		sockList = new ArrayList<Socket>();
    }

	public void SetSockHandle(SocketHandler _sockHandle) {
		sockHandle = _sockHandle;
	}

    public void StartSocketServer() {
		System.out.println("Starting socket server on port: " + port);
		
		try (ServerSocket serverSocket = new ServerSocket(port)) {	
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected on port: " + port);
					
				sockList.add(clientSocket);

				new Thread(() -> HandleKeyClient(clientSocket)).start();
			}
		
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

    void HandleKeyClient(Socket socket) {
		try { 
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				PrintWriter out = new PrintWriter(socket.getOutputStream());

				String data;
			while((data = in.readLine()) != null) {
				System.out.println("Received on port " + port + ": " + data);

				if(data.equals("END")) {
					break;
				}
				if(data.length() > 6 && data.substring(0, 6).equals("Append")) {
					String line = data.substring(6);
					int index = DataKey.AppendToKey(line);

					out.println("Index" + index);

					sockHandle.UpdateScanHandle();

					new Thread(() -> SendLatestIndex(index)).start();
					continue;
				}
				if(data.length() > 4 && data.substring(0, 5).equals("Clear")) {
					new Thread(() -> ClearForNewYear()).start();
				}
				if(data.length() > 3 && data.substring(0, 4).equals("CKey")) {
					new Thread(() -> ClearKey()).start();
				}
				if(data.length() > 5 && data.substring(0, 6).equals("CSheet")) {
					new Thread(() -> ClearSheet()).start();
				}
				if(data.length() > 5 && data.substring(0,6).equals("ISaved")) {
					new Thread(() -> SendIndexWriteConfirmed()).start();
				}
				if(data.length() > 5 && data.substring(0,6).equals("IError")) {
					new Thread(() -> SendIndexErrorMessage()).start();
				}
				if(data.length() > 4 && data.substring(0, 4).equals("Bell")) {
					String timeString = data.substring(4);
					new Thread(() -> TimeFile.UpdateBellFile(timeString)).start();
				}
				if(data.length() > 3 && data.substring(0, 4).equals("Time")) {
					new Thread(() -> SendCurrentBellTime(TimeFile.TimeFromFile(GlobalFilepaths.globalBellPath).toString())).start();
				}
				if(data.length() > 7 && data.substring(0, 8).equals("Shutdown")) {
					new Thread(() -> ShutdownPi()).start();
				}
		}

		sockList.remove(socket);

		} catch (IOException e) {
            
		}
    }

	void ClearForNewYear() {
		sockHandle.scanHandler.ClearScanHandler();
		DataKey.ClearDataKey();
		CsvHandler.ClearAllCsvs();
	}

	void ClearKey() {
		DataKey.ClearDataKey();
		sockHandle.scanHandler.ClearScanHandler();
		sockHandle.UpdateScanHandle();
	}

	void ClearSheet() {
		sockHandle.scanHandler.ClearScanHandler();
		CsvHandler.ClearAllCsvs();
	}

	void SendCurrentBellTime(String timeString) {
		for(int i = 0; i < sockList.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(sockList.get(i).getOutputStream(), true);
				out.println("Time" + timeString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void SendLatestIndex(int index) {
		for(int i = 0; i < sockList.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(sockList.get(i).getOutputStream(), true);
				out.println("Index" + index);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void SendIndexWriteConfirmed() {
		System.out.println("Sending IndexConfirmed...");
		for(int i = 0; i < sockList.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(sockList.get(i).getOutputStream(), true);
				out.println("ISuccess");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void SendIndexErrorMessage() {
		
		System.out.println("Sending IndexError...");
		for(int i = 0; i < sockList.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(sockList.get(i).getOutputStream(), true);
				out.println("IError");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	void ShutdownPi() {
		try {
			ServiceHandler.ShutDownSystem();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
