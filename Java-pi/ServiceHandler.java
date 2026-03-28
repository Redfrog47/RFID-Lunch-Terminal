
public class ServiceHandler {
    public static String readService = "rfid-reader.service";
    public static String writeService = "rfid-writer.service";

    public static void StartService(String serviceName) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "systemctl", "start", serviceName
        );

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if(exitCode != 0) {
            throw new RuntimeException("Failed to start service: " + serviceName);
        }

        System.out.println("Started: " + serviceName);
    }

    public static void StopService(String serviceName) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "systemctl", "stop", serviceName
        );

        Process process = processBuilder.start();

        int exitCode = process.waitFor();

        if(exitCode != 0) {
            throw new RuntimeException("Failed to stop service: " + serviceName);
        }

        System.out.println("Stopped: " + serviceName);
    }

    public static Boolean ServiceIsActive(String serviceName) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "systemctl", "is-active", serviceName
        );

        Process process = processBuilder.start();
        
        int exitCode = process.waitFor();

        if(exitCode == 0) {
            //System.out.println(serviceName + " is active");
        } else {
            //System.out.println(serviceName + " is inactive");
        }

        return exitCode == 0;
    }

    ///Blocks until the service is active
    public static void WaitForServiceActive(String serviceName) throws Exception {
        while (!ServiceIsActive(serviceName)) {
            Thread.sleep(100);
        }
    }

    public static void SwitchToReadService() throws Exception {
        if(ServiceIsActive(readService)) {
            System.out.println("Read service already running");
            return;
        }

        StopService(writeService);
        StartService(readService);

        System.out.println("Switched to read service");
    }

    public static void SwitchToWriteService() throws Exception {
        if(ServiceIsActive(writeService)) {
            System.out.println("Write service already running");
            return;
        }

        StopService(readService);
        StartService(writeService);

        System.out.println("Switched to write service");
    }

    ///Returns false if no services are running
    public static Boolean CheckServicesRunning() throws Exception {
        return ServiceIsActive(readService) || ServiceIsActive(writeService);
    }

    ///Restart correct service in case of a crash
    public static void EnsureServiceActive(WindowsConnectionHandler connection) throws Exception {
        if(!CheckServicesRunning()) {
            System.out.println("No service active");
            if(connection.CheckReadingStatus()) {
                StartService(readService);
            }
            else StartService(writeService);
        }

        Thread.sleep(10000);

        EnsureServiceActive(connection);
    }
}

