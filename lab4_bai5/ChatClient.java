
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5002);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(
                             socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                     new OutputStreamWriter(
                             socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            Thread receiver = new Thread(() -> {
                try {
                    String response;

                    while ((response = in.readLine()) != null) {
                        System.out.println("\n" + response);

                        if (response.equals("BYE")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Mat ket noi Server.");
                }
            });

            receiver.setDaemon(true);
            receiver.start();

            System.out.println("Da ket noi Chat Server!");
            System.out.println("Nhap nickname truoc khi chat.");
            System.out.println("Lenh: USERS, MSG <noi dung>, QUIT");

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                out.println(message);

                if (message.equals("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }
    }
}
