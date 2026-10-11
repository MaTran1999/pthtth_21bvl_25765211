
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.security.*;
import java.util.HexFormat;

public class FileServer {
    static final int PORT = 5008;
    static final Path UPLOAD = Paths.get("upload")
            .toAbsolutePath().normalize();

    public static void main(String[] args) throws Exception {
        Files.createDirectories(UPLOAD);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("File Server dang chay port " + PORT);
            System.out.println("Upload folder: " + UPLOAD);

            while (true) {
                try (Socket socket = server.accept()) {
                    socket.setSoTimeout(30000);
                    handle(socket);
                } catch (Exception e) {
                    System.out.println("Loi Client: " + e.getMessage());
                }
            }
        }
    }

    static void handle(Socket socket) throws Exception {
        DataInputStream in = new DataInputStream(
                new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(
                socket.getOutputStream());

        String name = in.readUTF();
        long size = in.readLong();
        String expectedHash = in.readUTF();

        System.out.println("File: " + name);
        System.out.println("Size: " + size + " bytes");

        if (name.isEmpty() || name.equals(".") ||
            name.equals("..") ||
            name.contains("/") || name.contains("\\") ||
            name.contains(":") ||
            !name.matches("[a-zA-Z0-9._-]+")) {
            out.writeUTF("ERR INVALID_FILENAME");
            out.flush();
            return;
        }

        if (size < 0 || size > 100_000_000L) {
            out.writeUTF("ERR INVALID_SIZE");
            out.flush();
            return;
        }

        if (!expectedHash.matches("[0-9a-fA-F]{64}")) {
            out.writeUTF("ERR INVALID_HASH");
            out.flush();
            return;
        }

        Path target = UPLOAD.resolve(name).normalize();

        if (!target.getParent().equals(UPLOAD)) {
            out.writeUTF("ERR INVALID_FILENAME");
            out.flush();
            return;
        }

        Path temp = Files.createTempFile(UPLOAD, "receiving-", ".tmp");

        out.writeUTF("READY");
        out.flush();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (OutputStream fileOut = Files.newOutputStream(temp)) {
                byte[] buffer = new byte[8192];
                long remaining = size;

                while (remaining > 0) {
                    int count = in.read(buffer, 0,
                            (int) Math.min(buffer.length, remaining));

                    if (count == -1) {
                        throw new EOFException("Thieu du lieu");
                    }

                    fileOut.write(buffer, 0, count);
                    digest.update(buffer, 0, count);
                    remaining -= count;
                }
            }

            String actualHash = HexFormat.of()
                    .formatHex(digest.digest());

            if (!actualHash.equalsIgnoreCase(expectedHash)) {
                out.writeUTF("ERR HASH_MISMATCH");
                out.flush();
                System.out.println("SHA-256 khong khop!");
                return;
            }

            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);

            out.writeUTF("OK");
            out.flush();

            System.out.println("Da luu: " + target);
            System.out.println("SHA-256: " + actualHash);
            System.out.println("Ket qua: OK");

        } finally {
            Files.deleteIfExists(temp);
        }
    }
}
