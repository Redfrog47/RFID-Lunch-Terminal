import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String ip = "192.168.7.2";

        ScanSocket scanSocket = new ScanSocket(5000, ip);
        SheetSocket sheetSocket = new SheetSocket(5001, ip);
        KeySocket keySocket = new KeySocket(5002, ip);

        GuiHandler guiHandler = new GuiHandler(scanSocket, sheetSocket, keySocket);

        keySocket.SetGuiHandler(guiHandler);

        new Thread(scanSocket::ConnectSocket).start();
        new Thread(sheetSocket::ConnectSocket).start();
		new Thread(keySocket::ConnectSocket).start();

        SwingUtilities.invokeLater(() -> {
            guiHandler.InitGuiOOP();
        });
    }
}
