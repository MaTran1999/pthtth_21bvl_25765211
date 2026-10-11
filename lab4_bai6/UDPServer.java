
import java.net.*;

public class UDPServer {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(6001)) {
            System.out.println("UDP Server dang chay port 6001");

            while (true) {
                byte[] buffer = new byte[128];

                DatagramPacket request = new DatagramPacket(
                    buffer, buffer.length
                );

                socket.receive(request);

                DatagramPacket response = new DatagramPacket(
                    request.getData(),
                    request.getLength(),
                    request.getAddress(),
                    request.getPort()
                );

                socket.send(response);
            }

        } catch (Exception e) {
            System.out.println("Loi UDP: " + e.getMessage());
        }
    }
}
