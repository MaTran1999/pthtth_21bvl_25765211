package datatime;

import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UdpDateServer {

    public static void main(String[] args) {

        int port = 6000;

        try (DatagramSocket socket = new DatagramSocket(port)) {

            System.out.println("UDP Server dang chay tai port " + port);

            byte[] buffer = new byte[1024];

            while (true) {

                DatagramPacket requestPacket =
                    new DatagramPacket(buffer, buffer.length);

                socket.receive(requestPacket);

                String request = new String(
                    requestPacket.getData(),
                    0,
                    requestPacket.getLength()
                );

                String response = processCommand(request);

                byte[] responseData = response.getBytes();

                DatagramPacket responsePacket =
                    new DatagramPacket(
                        responseData,
                        responseData.length,
                        requestPacket.getAddress(),
                        requestPacket.getPort()
                    );

                socket.send(responsePacket);
            }

        } catch (Exception e) {
            System.err.println("Loi UDP Server: " + e.getMessage());
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

            default:
                return "ERR INVALID_COMMAND";
        }
    }
}