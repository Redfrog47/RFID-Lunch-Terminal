import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SheetSocket {
	int port;
	String ip;

	private Socket socket;

	private PrintWriter out;
	private BufferedReader in;

	private boolean fileManagerOpen = false;
	
	public SheetSocket(int _port, String _ip) {
		port = _port;
		ip = _ip;
	}
	
	void ConnectSocket() {
		try {
			//System.out.println("Creating sheet socket");
			socket = new Socket(ip, port);

			out = new PrintWriter(socket.getOutputStream(), true);
        	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
			String sheetData = "";
       		String line;

       		while ((line = in.readLine()) != null) {
				System.out.println(line);

            	if (line.length() > 2 && line.substring(0,3).equals("END")) {
					String fileName = line.substring(3);
                	SaveSheetData(sheetData, fileName);
                	sheetData = "";
					continue;
            	}

				if(line.length() > 4 && line.substring(0,5).equals("CHECK")) {
					if(fileManagerOpen) {
						out.println("5");
					} else {
						out.println("6");
					}
				}

			sheetData += line + "\n";
        }

   		} catch (SocketException e) {
			RetryConnection();
		} catch (IOException e) {
			e.printStackTrace();
       		System.out.println("Connection lost");
  		}
	}

	public void PullRequested() {
		if(out != null) {
			out.println("1");
		}
	}

	public void PullAllSheets() {
		if(out != null) {
			out.println("2");
		}
	}

	
	void SaveSheetData(String sheetData, String fileName) {
		try {
			Path baseDir = Paths.get(System.getenv("APPDATA"), "CardTerminal");

			Path path = baseDir.resolve(fileName);

			Files.createDirectories(path.getParent());

		    Files.write(path, sheetData.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		    //System.out.println("Spreadsheet written");
		} catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("Error saving spreadsheet");
		}

	}

	public void OnFileManagerOpened() {
		fileManagerOpen = true;

		if(out != null) {
			out.println("3");
		}
	}

	public void OnFileManagerClosed() {
		fileManagerOpen = false;

		if(out != null) {
			out.println("4");
		}
	}


}
