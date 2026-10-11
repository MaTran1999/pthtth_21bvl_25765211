
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LogClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5007);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                 new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.print("Nhap Client ID: ");
            String clientId = scanner.nextLine();

            out.println("HELLO " + clientId);

            String response = in.readLine();
            System.out.println("Server: " + response);

            if (response == null || !response.startsWith("OK HELLO")) {
                return;
            }

            System.out.println("Nhap tin nhan, QUIT de thoat.");

            while (true) {
                System.out.print("Tin nhan: ");
                String message = scanner.nextLine();

                out.println(message);

                response = in.readLine();

                if (response == null) {
                    System.out.println("Server da ngat ket noi.");
                    break;
                }

                System.out.println("Server: " + response);

                if (message.equals("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
