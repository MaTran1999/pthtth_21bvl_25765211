
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.HexFormat;
import java.util.Scanner;

public class FileClient {
    static final int PORT = 5008;

    static String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream in = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int n;

            while ((n = in.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Nhap duong dan file: ");
            Path path = Paths.get(scanner.nextLine());

            if (!Files.isRegularFile(path)) {
                System.out.println("ERR FILE_NOT_FOUND");
                return;
            }

            String name = path.getFileName().toString();
            long size = Files.size(path);
            String hash = sha256(path);

            System.out.println("File: " + name);
            System.out.println("Size: " + size + " bytes");
            System.out.println("SHA-256: " + hash);

            try (Socket socket = new Socket("127.0.0.1", PORT)) {
                socket.setSoTimeout(30000);

                DataOutputStream out = new DataOutputStream(
                        new BufferedOutputStream(socket.getOutputStream()));
                DataInputStream in = new DataInputStream(
                        socket.getInputStream());

                out.writeUTF(name);
                out.writeLong(size);
                out.writeUTF(hash);
                out.flush();

                String status = in.readUTF();

                if (!status.equals("READY")) {
                    System.out.println("Server: " + status);
                    return;
                }

                try (InputStream fileIn = Files.newInputStream(path)) {
                    byte[] buffer = new byte[8192];
                    int n;

                    while ((n = fileIn.read(buffer)) != -1) {
                        out.write(buffer, 0, n);
                    }
                }

                out.flush();

                System.out.println("Server: " + in.readUTF());
            }

        } catch (Exception e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}
