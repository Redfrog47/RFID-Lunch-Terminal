import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GuiHandler {

    ScanSocket scanSocket;
    SheetSocket sheetSocket;
    KeySocket keySocket;

    JFrame mainFrame;
    JFrame keyFrame;

    CardLayout mainCardLayout;
    JPanel mainCardPanel;

    CardLayout cardLayout;
    JPanel cardPanel;

    JPanel mainFrameMainPanel;
    JPanel keySetupButtonPanel;
    JPanel closedPanel;

    JTextField idField;

    JLabel currentBell;

    public GuiHandler(ScanSocket _scanSocket, SheetSocket _sheetSocket, KeySocket _keySocket) {
        scanSocket = _scanSocket;
        sheetSocket = _sheetSocket;
        keySocket = _keySocket;
    }

    public void InitGuiOOP() {
        mainFrame = CreateMainFrameOOP();
        mainFrame.setVisible(true);
    }

    JFrame CreateMainFrameOOP() {
        JFrame mainFrame = new JFrame("Lunch Terminal");
        mainFrame.setSize(400, 300);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                CloseMainFrame();
            }
        });

        mainFrame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
        mainFrame.setIconImage(icon.getImage());

        mainFrame.setLayout(new BorderLayout(10, 10));

        mainCardLayout = new CardLayout();
        mainCardPanel = new JPanel(mainCardLayout);

        mainFrameMainPanel = new JPanel(); 
        mainFrameMainPanel.setLayout(new GridBagLayout());
        mainFrameMainPanel.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(30, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainFrameMainPanel.add(new JLabel("ID scan:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        mainFrameMainPanel.add(usernameField, gbc);

        usernameField.addActionListener(e -> {
            String text = usernameField.getText();
            usernameField.setText("");
            scanSocket.SendIdScan(text);
        });
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JButton button = new JButton("Pull sheet");
        mainFrameMainPanel.add(button, gbc);

        button.addActionListener(e -> {
            sheetSocket.PullRequested();
        });

        keySetupButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton keySetupButton = new JButton("Device Manager");
        keySetupButtonPanel.add(keySetupButton);

        keySetupButton.addActionListener(e -> {
            if(keyFrame == null) {
                NewKeyFrame();
            } else if (keyFrame != null) {
                keyFrame.dispose();
                NewKeyFrame();
            }
        });

        closedPanel = new JPanel();
        JLabel message = new JLabel("Close Device Manager use terminal");

        closedPanel.add(message, BorderLayout.CENTER);

        JPanel combined = new JPanel(new BorderLayout());
        

        combined.add(mainFrameMainPanel, BorderLayout.NORTH);
        combined.add(keySetupButtonPanel, BorderLayout.SOUTH);

        mainCardPanel.add(combined, "Main");
        mainCardPanel.add(closedPanel, "Closed");

        mainFrame.add(mainCardPanel, BorderLayout.CENTER);

        mainCardLayout.show(mainCardPanel, "Main");

        return mainFrame;
    }

    void HideMainFrame() {
        mainCardLayout.show(mainCardPanel, "Closed");
    }

    void ShowMainFrame() {
        mainCardLayout.show(mainCardPanel, "Main");
    }

    void CloseMainFrame() {
        scanSocket.OnMainFrameClosed();

        System.exit(0);
    }

    void NewKeyFrame() {
        keyFrame = CreateKeyFrameOOP();
        keyFrame.setVisible(true);
        sheetSocket.OnFileManagerOpened();
        scanSocket.OnKeyFrameOpened();

        HideMainFrame();
    }

    void CloseKeyFrame() {
        keyFrame = null;
        sheetSocket.OnFileManagerClosed();
        scanSocket.OnKeyFrameClosed();

        ShowMainFrame();
    }

    JFrame CreateKeyFrameOOP() {
        JFrame keyFrame = new JFrame("Device Manager");
        keyFrame.setSize(300, 400);
        keyFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 

        keyFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                CloseKeyFrame();
                keyFrame.dispose();
            }
        });

        keyFrame.setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
        keyFrame.setIconImage(icon.getImage());

        keyFrame.setLayout(new BorderLayout(10, 10));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        currentBell = new JLabel("Current time is: not working :(");
        keySocket.RequestCurrentBellTime();

        JPanel mainPagePanel = new JPanel(new BorderLayout());

        JPanel panel = new JPanel(); 
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(20, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JButton add = new JButton("Add Student");
        panel.add(add, gbc);

        add.addActionListener(e -> 
            MoveToAddPage()
        );

        gbc.gridy = 1;
        JButton send = new JButton("Erase Data");
        panel.add(send, gbc);

        send.addActionListener(e -> 
            cardLayout.show(cardPanel, "Clear")
        );

        gbc.gridy = 2;
        JButton pullAll = new JButton("Pull all Sheets");
        panel.add(pullAll, gbc);

        pullAll.addActionListener(e -> {
            sheetSocket.PullAllSheets();
            cardLayout.show(cardPanel, "Panel");
        });

        gbc.gridy = 3;
        JButton bellUpdateButton = new JButton("Change Lunch Bell");
        panel.add(bellUpdateButton, gbc);

        bellUpdateButton.addActionListener(e -> {
            currentBell.setText("Current bell time: " + keySocket.CurrentBellTime());
            cardLayout.show(cardPanel, "Bell");
        });

        gbc.gridy = 4;
        JButton shutdownButton = new JButton("Shutdown Pi");
        panel.add(shutdownButton, gbc);

        shutdownButton.addActionListener(e -> {
            ConfirmPanel(cardLayout, cardPanel, "Are you sure you want to shutdown the Pi?", e1 -> {
                keySocket.ShutdownPi();
                keyFrame.dispose();
                mainFrame.dispose();

                System.exit(0);
            });
        });

        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton close = new JButton("Close");
        closePanel.add(close);

        mainPagePanel.add(closePanel, BorderLayout.SOUTH);

        close.addActionListener(e -> {
            CloseKeyFrame();
            keyFrame.dispose();
        }
        );

        mainPagePanel.add(panel, BorderLayout.NORTH);

        JPanel cancelPanel = new JPanel(new BorderLayout());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancel = new JButton("Cancel");
        bottomPanel.add(cancel, gbc);

        cancelPanel.add(bottomPanel, BorderLayout.SOUTH);

        cancel.addActionListener(e -> 
            cardLayout.show(cardPanel, "Panel")
        );

        JPanel waitForScan = new JPanel();
        JLabel message = new JLabel("Scan card to configure data");
        waitForScan.add(message);

        JPanel stuAddPage = new JPanel(new BorderLayout());

        JPanel stuAddContents = new JPanel();
        stuAddContents.setLayout(new GridBagLayout());
        stuAddContents.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new java.awt.Insets(15, 5, 5, 5);
        gbc2.anchor = GridBagConstraints.WEST;

        idField = new JTextField(15);
        JTextField fName = new JTextField(15);
        JTextField lName = new JTextField(15);
        JButton sendButton = new JButton("Send");

        idField.addActionListener(e -> {
            StudentAddedOOP(idField, fName, lName);
        });

        fName.addActionListener(e -> {
            StudentAddedOOP(idField, fName, lName);
        });

        lName.addActionListener(e -> {
            StudentAddedOOP(idField, fName, lName);
        });

        sendButton.addActionListener(e -> {
            StudentAddedOOP(idField, fName, lName);
        });

        JLabel stuIdLabel = new JLabel("Student ID:");
        JLabel fNameLabel = new JLabel("First name:");
        JLabel lNameLabel = new JLabel("Last name:");

        gbc2.gridx = 0;
        gbc2.gridy = 0;
        stuAddContents.add(stuIdLabel, gbc2);

        gbc2.gridx = 1;
        stuAddContents.add(idField, gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 1;
        stuAddContents.add(fNameLabel, gbc2);

        gbc2.gridx = 1;
        stuAddContents.add(fName, gbc2);

        gbc2.gridx = 0;
        gbc2.gridy = 2;
        stuAddContents.add(lNameLabel, gbc2);
        
        gbc2.gridx = 1;
        stuAddContents.add(lName, gbc2);

        gbc2.gridy = 3;
        stuAddContents.add(sendButton, gbc2);

        stuAddPage.add(stuAddContents, BorderLayout.NORTH);
        
        JPanel bottomPanel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancel2 = new JButton("Cancel");
        bottomPanel2.add(cancel2);

        stuAddPage.add(bottomPanel2, BorderLayout.SOUTH);

        cancel2.addActionListener(e -> 
            cardLayout.show(cardPanel, "Panel")
        );

        JPanel yearClearPage = new JPanel(new BorderLayout());

        JPanel yearClearContents = new JPanel();
        yearClearContents.setLayout(new GridBagLayout());
        yearClearContents.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));

        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.insets = new java.awt.Insets(15, 5, 5, 5);
        gbc3.anchor = GridBagConstraints.WEST;

        JLabel warning = new JLabel("What would you like to clear?");
        JButton clearAll = new JButton("Key and Sheets");
        JButton clearKey = new JButton("Key");
        JButton clearSheets = new JButton("Sheets");
        
        clearAll.addActionListener(e -> {
            ConfirmPanel(cardLayout, cardPanel, "Clear Data and Sheets?", e1 -> {
                ClearDataForNewYear(keySocket);
                cardLayout.show(cardPanel, "Panel");
            });
        });

        clearKey.addActionListener(e -> {
            ConfirmPanel(cardLayout, cardPanel, "Clear Key?", e1 -> {
                ClearKey(keySocket);
                cardLayout.show(cardPanel, "Panel");
            });
        });

        clearSheets.addActionListener(e -> {
            ConfirmPanel(cardLayout, cardPanel, "Clear all Sheets?", e1 -> {
                ClearSheet(keySocket);
                cardLayout.show(cardPanel, "Panel");
            });
        });
        
        gbc3.gridx = 0;
        gbc3.gridy = 0;
        yearClearContents.add(warning, gbc3);

        gbc3.anchor = GridBagConstraints.CENTER;

        gbc3.gridx = 0;
        gbc3.gridy = 1;
        yearClearContents.add(clearAll, gbc3);

        gbc3.gridy = 2;
        yearClearContents.add(clearKey, gbc3);

        gbc3.gridy = 3;
        yearClearContents.add(clearSheets, gbc3);

        yearClearPage.add(yearClearContents, BorderLayout.NORTH);

        JPanel bottomPanel3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancel3 = new JButton("Cancel");
        bottomPanel3.add(cancel3);

        yearClearPage.add(bottomPanel3, BorderLayout.SOUTH);

        cancel3.addActionListener(e -> 
            cardLayout.show(cardPanel, "Panel")
        );

        JPanel bellUpdatePage = new JPanel(new BorderLayout());

        JPanel bellContents = new JPanel();
        bellContents.setLayout(new GridBagLayout());
        bellContents.setBorder(BorderFactory.createEmptyBorder(12, 5, 12, 5));

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.insets = new java.awt.Insets(15, 5, 5, 5);
        gbc4.anchor = GridBagConstraints.WEST;

        JTextField timeField = new JTextField(15);
        JLabel timeLabel = new JLabel("Enter the New Lunch end time");
        JLabel timeLabel2 = new JLabel("In MILITARY TIME");

        gbc4.gridx = 0;
        gbc4.gridy = 0;

        bellContents.add(currentBell, gbc4);

        gbc4.gridy = 1;

        bellContents.add(timeLabel, gbc4);

        gbc4.gridy = 2;

        bellContents.add(timeLabel2, gbc4);

        gbc4.gridy = 3;

        bellContents.add(timeField, gbc4);

        timeField.addActionListener(e -> {
            keySocket.UpdateBellTime(timeField.getText());
            timeField.setText("");
            cardLayout.show(cardPanel, "Panel");
            new Thread(() -> UpdateBellTimeAfterDelay()).start();
        });

        

        JPanel bottomPanel4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancel4 = new JButton("Cancel");
        bottomPanel4.add(cancel3);


        cancel4.addActionListener(e -> 
            cardLayout.show(cardPanel, "Panel")
        );

        bellUpdatePage.add(bellContents, BorderLayout.NORTH);
        bellUpdatePage.add(bottomPanel4 , BorderLayout.SOUTH);

        cardPanel.add(mainPagePanel, "Panel");
        cardPanel.add(cancelPanel, "Cancel");
        cardPanel.add(waitForScan, "Wait");
        cardPanel.add(stuAddPage, "Add");
        cardPanel.add(yearClearPage, "Clear");
        cardPanel.add(bellUpdatePage, "Bell");

        keyFrame.add(cardPanel, BorderLayout.CENTER);
        
        return keyFrame;
    }

    public void StudentAddedOOP(JTextField id, JTextField fName, JTextField lName) {
        if(id.getText().equals("")) {
            return;
        }
        if(fName.getText().equals("")) {
            return;
        }
        if(lName.getText().equals("")) {
            return;
        }

        String line = id.getText() + "," + fName.getText() + " " + lName.getText();

        id.setText("");
        fName.setText("");
        lName.setText("");
        
        cardLayout.show(cardPanel, "Wait");

        new Thread(() -> keySocket.AppendLineToKey(line)).start();
    }

    public void OnScanConfirmed() {
        SwingUtilities.invokeLater(() -> {
            if(keyFrame != null) {
                MoveToAddPage();
            }
        });
    }

    public void OnBadScan() {
        if(keyFrame != null) {
            ConfirmPanel(cardLayout, cardPanel, "Bad data entered. Please redo", e -> {
                MoveToAddPage();
            });
        }
    }

    void MoveToAddPage() {
        cardLayout.show(cardPanel, "Add");

        SwingUtilities.invokeLater(new Runnable() {
        @Override
            public void run() {
                idField.requestFocusInWindow();
            }
        });
    }

    void UpdateBellTimeAfterDelay() {
        keySocket.RequestCurrentBellTime();
        try {
            Thread.sleep(1000);
            currentBell.setText("Current bell time: " + keySocket.CurrentBellTime());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void ConfirmPanel(CardLayout cardLayout, JPanel cardPanel, String name, ActionListener action) {
        JPanel page = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(); 
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 40, 10, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(20, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel label = new JLabel(name);
        panel.add(label, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(action);
        panel.add(confirmButton, gbc);

        page.add(panel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancel = new JButton("Cancel");
        bottomPanel.add(cancel);

        page.add(bottomPanel, BorderLayout.SOUTH);

        cancel.addActionListener(e -> 
            cardLayout.show(cardPanel, "Panel")
        );

        cardPanel.add(page, name);
        cardLayout.show(cardPanel, name);
    }

    public static void StudentAdded(JTextField id, JTextField fName, JTextField lName, KeySocket keySocket) {
        if(id.getText().equals("")) {
            return;
        }
        if(fName.getText().equals("")) {
            return;
        }
        if(lName.getText().equals("")) {
            return;
        }

        String line = id.getText() + "," + fName.getText() + " " + lName.getText();

        id.setText("");
        fName.setText("");
        lName.setText("");
        

        new Thread(() -> keySocket.AppendLineToKey(line)).start();
    }

    public static void ClearDataForNewYear(KeySocket keySocket) {
        new Thread(() -> keySocket.ClearForNewYear()).start();
    }

    public static void ClearKey(KeySocket keySocket) {
        keySocket.ClearKey();
    }

    public static void ClearSheet(KeySocket keySocket) {
        keySocket.ClearSheet();
    }
}
