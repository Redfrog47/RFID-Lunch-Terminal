import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketHandler {
	int port;
	
	ScanHandler scanHandler;

	private ArrayList<Socket> sockList;
	
	public SocketHandler(int _port) {
		port = _port;
		scanHandler = new ScanHandler(GlobalFilepaths.globalSheetPath);

		sockList = new ArrayList<Socket>();
	}
	
	public void StartSocketServer() {
		System.out.println("Starting socket server on port: " + port);
		
		while(true) {
			try (ServerSocket serverSocket = new ServerSocket(port)) {
				
				while(true) {
					Socket clientSocket = serverSocket.accept();
					System.out.println("Client connected on port: " + port);
					
					sockList.add(clientSocket);

					new Thread(() -> HandleClient(clientSocket)).start();
				}
		
		} catch (IOException e) {
            e.printStackTrace();
			}
		}
	}
	
	private void HandleClient(Socket client) {
		try ( BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream()))) {
		
		String data;
		while((data = in.readLine()) != null) {
			System.out.println("Received on port " + port + ": " + data);
			HandleInput(data);
		}
		
		System.out.println("Client disconnected");

		sockList.remove(client);
		
		} catch (SocketException se) {
			sockList.remove(client);
		}
		catch (IOException e) {
            //System.out.println("Error, client disconnected " + e.getMessage());
		}
	}

	//Assumes that data beginning with 'P' is sent locally from the Pi
	//Data beginning with 'U' assumed to be sent over USB
	//Data beginning with 'T' assumed to be sent over USB to sync time
	void HandleInput(String data) {
		char deviceId = data.charAt(0);
		data = data.substring(1);

		if(deviceId == 'P') {
			int cardIndex = GetInputInt(data);
            System.out.println("Received from Pi: " + Integer.toString(cardIndex));
            
            if(cardIndex == -2) {
            	System.out.println("Clearing sheet from admin ID");
            	scanHandler.ClearScanHandler();
            	SheetHandler.ClearSheet(GlobalFilepaths.globalSheetPath);
            } else if(cardIndex == -3) {
            	System.out.println("Entering key setup with admin ID");
				DataKey.CreateDataKeyFile("test");	
            } else if(cardIndex >= 0 && cardIndex <= scanHandler.maxIndex()) {
            	scanHandler.OnCardScanned(cardIndex);
            } else {
            	System.out.println("Invalid card scanned");
            }

		} else if (deviceId == 'U') {
			int studentId = GetInputInt(data);
			//System.out.println("Looking for student #" + studentId);
			scanHandler.ScanStudentById(studentId);
		} else if (deviceId == 'T') {
			try {
				DateTimeHandler.FindTimeDriftAndResetSystemTime(data);

				PrintToAllSocks("Connected");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(deviceId == 'S') {
			PrintToAllSocks(data);
		}
		else {
			System.out.println("Error with input data");
		}
		scanHandler.SaveToSheet(GlobalFilepaths.globalSheetPath);

		//DebugPrintToAllSocks(data);
	}
	
	int GetInputInt(String input) {
		int output;
		try {
			output = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("Invalid int entered");
		    output = -1;
		}
		
		return output;
    }

	void UpdateScanHandle() {
		scanHandler.UpdateKey();
	}

	void PrintToAllSocks(String data) {
		for(int i = 0; i < sockList.size(); i++) {
			try {
				PrintWriter out = new PrintWriter(sockList.get(i).getOutputStream(), true);
				out.println(data);
			} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
}
