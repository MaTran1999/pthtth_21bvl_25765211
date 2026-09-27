package LAB4.bai2.src.tcp;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DigitClient {

    public static void main(String[] args) {

        String host = "localhost";
        int port = 5000;

        try (
            Socket socket = new Socket(host, port);

            BufferedReader console = new BufferedReader(
                new InputStreamReader(
                    System.in,
                    StandardCharsets.UTF_8
                )
            );

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

            System.out.println("Đã kết nối Server.");
            System.out.println("Nhập số từ 0-9 hoặc QUIT để thoát:");

            String request;

            while ((request = console.readLine()) != null) {

                out.println(request);

                String response = in.readLine();

                System.out.println("Server: " + response);

                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi: " + e.getMessage());
        }
    }
}