
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CalcClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5001);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(
                     socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(
                 new OutputStreamWriter(
                     socket.getOutputStream(), StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            System.out.println("Da ket noi den Server!");
            System.out.println("Nhap: CALC <toan_tu> <so1> <so2>");
            System.out.println("Nhap QUIT de thoat.");

            while (true) {
                System.out.print("Nhap: ");
                String request = scanner.nextLine();

                out.println(request);

                if (request.equals("QUIT")) {
                    System.out.println("Da ngat ket noi.");
                    break;
                }

                String response = in.readLine();

                if (response == null) {
                    System.out.println("Server da ngat ket noi.");
                    break;
                }

                System.out.println("Server tra ve: " + response);
            }

        } catch (IOException e) {
            System.out.println("Loi ket noi: " + e.getMessage());
        }
    }
}
