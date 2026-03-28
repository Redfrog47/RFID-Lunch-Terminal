import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WindowsConnectionHandler {
    int port;

	private PrintWriter out;

	private boolean reading = true;

    public WindowsConnectionHandler(int _port) {
        port = _port;
    }

    public void StartSocketServer() {
		System.out.println("Starting socket server on port: " + port);
		
		try (ServerSocket serverSocket = new ServerSocket(port)) {
				
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected on port: " + port);

				out = new PrintWriter(clientSocket.getOutputStream(), true);
					
				new Thread(() -> HandleWindowsClient(clientSocket)).start();
			}
		
		} catch (IOException e) {
            e.printStackTrace();
		}
	}

    void HandleWindowsClient(Socket socket) {
        
        try ( BufferedReader in = new BufferedReader(
				new InputStreamReader(socket.getInputStream()))) {
		
		String data;
		while((data = in.readLine()) != null) {
            int inputInt = GetInputInt(data);
            System.out.println("Received on port " + port + ": " + data);

            if(inputInt == 1) {
				System.out.println(LoadSpreadSheetToString(GlobalFilepaths.globalSheetPath) + "END" + GlobalFilepaths.globalSheetPath);
                out.println(LoadSpreadSheetToString(GlobalFilepaths.globalSheetPath) + "END" + GlobalFilepaths.globalSheetPath);
            }

			if(inputInt == 2) {
				List<String> filepaths = CsvHandler.GetAllCsvFiles();

				for(int i = 0; i < filepaths.size(); i++) {
					out.println(LoadSpreadSheetToString(filepaths.get(i)) + "END" + filepaths.get(i));
				}
			}

			if(inputInt == 3) {
				try {
					ServiceHandler.SwitchToWriteService();
					reading = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if(inputInt == 4) {
				try {
					ServiceHandler.SwitchToReadService();
					reading = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if(inputInt == 5) {
				reading = false;
			}

			if(inputInt == 6) {
				reading = true;
			}
            
		}
		
		System.out.println("Client disconnected");
		
		} catch (IOException e) {
            
		}
    }

	//Assumes if no socket connected, then reading
	public boolean CheckReadingStatus() {
		if(out == null) { return true; }

		out.println("CHECK");

		System.out.println("Reading: " + reading);
		return reading;
	}

	//Should return null if no file found?
    String LoadSpreadSheetToString(String filepath) {
        String output = "";

        Path path = Paths.get(filepath);
		
		InputStreamReader inputStreamRead = null;
		try {
			inputStreamRead = new InputStreamReader(Files.newInputStream(path));
		} catch (IOException e) {
			return "";
		}
		BufferedReader in = new BufferedReader(inputStreamRead);

        String data;
        try {
            while ((data = in.readLine()) != null) {
                output += data + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
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

	ArrayList<String> LoadAllCsvsToString() {
		ArrayList<String> strings = new ArrayList<String>();

		return strings;
	}
}