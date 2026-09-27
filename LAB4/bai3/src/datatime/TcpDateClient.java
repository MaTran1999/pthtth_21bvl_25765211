package datatime;

import java.io.*;
import java.net.*;

public class TcpDateClient {

    public static void main(String[] args) {

        try (
            Socket socket = new Socket("localhost", 5000);

            BufferedReader console = new BufferedReader(
                new InputStreamReader(System.in)
            );

            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true
            )
        ) {

            System.out.println("Da ket noi TCP Server.");
            System.out.println("Lenh: DATE, TIME, DATETIME, QUIT");

            String request;

            while ((request = console.readLine()) != null) {

                out.println(request);

                String response = in.readLine();

                if (response == null) {
                    System.out.println("Server da dung!");
                    break;
                }

                System.out.println("Server: " + response);

                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Khong ket noi duoc TCP Server.");
        }
    }
}