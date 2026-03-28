import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ScanSocket {
    int port;
    String ip;

	Socket socket;

	private PrintWriter out;
	private BufferedReader in;

    public ScanSocket (int _port, String _ip){
        port = _port;
        ip = _ip;
    }

    void ConnectSocket() {
		try {
			//System.out.println("Creating scan socket");
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
			String data;
			while ((data = in.readLine()) != null) {
				System.out.println("ScanSocket received: " + data);
			}
		} catch (SocketException e) {
			RetryConnection();
		} catch (IOException e) {
			e.printStackTrace();
       		System.out.println("Connection lost");
  		}
	}

	public void SendIdScan(String id) {
		out.println("U" + id);
	}
}