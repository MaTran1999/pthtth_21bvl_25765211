package datatime;

import java.net.*;
import java.util.Scanner;

public class UdpDateClient {

    public static void main(String[] args) {

        try (
            DatagramSocket socket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in)
        ) {

            InetAddress address =
                InetAddress.getByName("localhost");

            System.out.println("UDP Client");
            System.out.println("Lenh: DATE, TIME, DATETIME");
            System.out.println("Nhap EXIT de dung client.");

            while (true) {

                String request = scanner.nextLine();

                if (request.equalsIgnoreCase("EXIT")) {
                    break;
                }

                byte[] data = request.getBytes();

                DatagramPacket requestPacket =
                    new DatagramPacket(
                        data,
                        data.length,
                        address,
                        6000
                    );

                socket.send(requestPacket);

                byte[] buffer = new byte[1024];

                DatagramPacket responsePacket =
                    new DatagramPacket(buffer, buffer.length);

                socket.receive(responsePacket);

                String response = new String(
                    responsePacket.getData(),
                    0,
                    responsePacket.getLength()
                );

                System.out.println("Server: " + response);
            }

        } catch (Exception e) {
            System.err.println(
                "Loi UDP Client: " + e.getMessage()
            );
        }
    }
}