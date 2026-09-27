package datatime;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TcpDateServer {

    public static void main(String[] args) {

        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("TCP Server dang chay tai port " + port);

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("TCP Client da ket noi.");

                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Loi TCP Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {

        try (
            socket;
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true
            )
        ) {

            String request;

            while ((request = in.readLine()) != null) {

                String response = processCommand(request);

                out.println(response);

                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client bi mat ket noi.");
        }
    }

    private static String processCommand(String command) {

        LocalDateTime now = LocalDateTime.now();

        switch (command.toUpperCase()) {

            case "DATE":
                return now.format(
                    DateTimeFormatter.ofPattern("dd MM yyyy")
                );

            case "TIME":
                return now.format(
                    DateTimeFormatter.ofPattern("HH mm ss")
                );

            case "DATETIME":
                return now.format(
                    DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss")
                );

            case "QUIT":
                return "BYE";

            default:
                return "ERR INVALID_COMMAND";
        }
    }
}