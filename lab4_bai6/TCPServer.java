
import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(6000)) {
            System.out.println("TCP Server dang chay port 6000");

            while (true) {
                try (Socket socket = server.accept()) {
                    InputStream in = socket.getInputStream();
                    OutputStream out = socket.getOutputStream();

                    byte[] data = new byte[128];

                    while (true) {
                        int total = 0;

                        while (total < data.length) {
                            int n = in.read(data, total, data.length - total);

                            if (n == -1) {
                                break;
                            }

                            total += n;
                        }

                        if (total < data.length) {
                            break;
                        }

                        out.write(data);
                        out.flush();
                    }

                } catch (IOException e) {
                    System.out.println("Client ngat ket noi.");
                }
            }

        } catch (IOException e) {
            System.out.println("Loi TCP: " + e.getMessage());
        }
    }
}
