
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class LogServer {
    private static final int PORT = 5007;
    private static final ExecutorService pool =
            Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try {
            Files.createDirectories(Paths.get("data", "logs"));
        } catch (IOException e) {
            System.out.println("Loi tao thu muc: " + e.getMessage());
            return;
        }

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Log Server dang chay port " + PORT);

            while (true) {
                Socket socket = server.accept();
                pool.execute(() -> handleClient(socket));
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        try (Socket client = socket;
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(
                     client.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                 new OutputStreamWriter(
                     client.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String hello = in.readLine();

            if (hello == null || !hello.matches("HELLO [a-zA-Z0-9_-]+")) {
                out.println("ERR INVALID_HELLO");
                return;
            }

            String clientId = hello.substring(6);
            String remote = client.getRemoteSocketAddress().toString();

            Path logFile = Paths.get(
                "data", "logs", clientId + ".txt"
            );

            out.println("OK HELLO " + clientId);
            System.out.println(clientId + " da ket noi: " + remote);

            String message;

            while ((message = in.readLine()) != null) {
                if (message.equals("QUIT")) {
                    out.println("BYE");
                    break;
                }

                String timestamp = LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                );

                String log = timestamp + " | " + remote +
                             " | " + message;

                synchronized (LogServer.class) {
                    Files.writeString(
                        logFile,
                        log + System.lineSeparator(),
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                    );
                }

                out.println("OK SAVED");
                System.out.println(clientId + ": " + message);
            }

            System.out.println(clientId + " da ngat ket noi.");

        } catch (IOException e) {
            System.out.println("Loi Client: " + e.getMessage());
        }
    }
}
