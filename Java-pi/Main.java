
public class Main {	
	public static void main(String[] args) {
		String date = ActiveFileHandler.GetDate();
		GlobalFilepaths.SetGlobalSheetPath(date);

		TimeFile.LoadBellTime();

		SocketHandler socketHandle = new SocketHandler(5000);
		WindowsConnectionHandler windowsConnection = new WindowsConnectionHandler(5001);
		KeySocketHandler keySocket = new KeySocketHandler(5002);

		keySocket.SetSockHandle(socketHandle);

		ActiveFileHandler activeFileHandler = new ActiveFileHandler(socketHandle);

		socketHandle.activeFileHandler = activeFileHandler;

		new Thread(socketHandle::StartSocketServer).start();
		new Thread(windowsConnection::StartSocketServer).start();
		new Thread(keySocket::StartSocketServer).start();

		new Thread(() -> {
			try {
				activeFileHandler.UpdateDate();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();

		new Thread(() -> {
			try {
				ServiceHandler.EnsureServiceActive(windowsConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}
}


