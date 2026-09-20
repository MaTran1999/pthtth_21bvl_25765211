package thuchanh3.src;

import java.io.*;
import java.nio.file.*;

public class BinaryFileCopy {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Cách dùng: java BinaryFileCopy <nguồn> <đích>");
            return;
        }

        Path source = Path.of(args[0]);
        Path target = Path.of(args[1]);

        InputStream input = new BufferedInputStream(
            Files.newInputStream(source)
        );

        OutputStream output = new BufferedOutputStream(
            Files.newOutputStream(target)
        );

        byte[] buffer = new byte[8192];

        int bytesRead;
        long totalBytes = 0;

        while ((bytesRead = input.read(buffer)) != -1) {

            output.write(buffer, 0, bytesRead);

            totalBytes = totalBytes + bytesRead;
        }

        input.close();
        output.close();

        System.out.println("Đã sao chép " + totalBytes + " byte.");
    }
}