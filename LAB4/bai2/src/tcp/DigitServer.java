package LAB4.bai2.src.tcp;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DigitServer {

    private static final String[] DIGITS = {
        "không", "một", "hai", "ba", "bốn",
        "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {

        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Server đang chạy tại port " + port);
            System.out.println("Đang chờ client kết nối...");

            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("Client đã kết nối.");

                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {

        try (
            socket;
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );

            PrintWriter out = new PrintWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                ),
                true
            )
        ) {

            String request;

            while ((request = in.readLine()) != null) {

                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("BYE");
                    break;
                }

                if (request.length() == 1 &&
                    request.charAt(0) >= '0' &&
                    request.charAt(0) <= '9') {

                    int digit = request.charAt(0) - '0';

                    out.println(DIGITS[digit]);

                } else {
                    out.println("ERR INVALID_DIGIT");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}