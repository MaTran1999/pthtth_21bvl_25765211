package thuchanh2.JavaIOStream.src;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class TextFileDemo {
    public static void main(String[] args) throws Exception {

        //Đường dẫn tới file ghi_chu.txt
        Path file = Path.of("data", "ghi_chu.txt");

        // Tạo thư mục data nếu chưa có
        Files.createDirectories(file.getParent());

        //Ghi vào file

        BufferedWriter writer = Files.newBufferedWriter(
            file,
            StandardCharsets.UTF_8
        );

        writer.write("Java I/O làm việc với các luồng dữ liệu.");
        writer.newLine();

        writer.write("BufferedWriter giúp ghi văn bản hiệu quả.");
        writer.newLine();

        writer.write("UTF-8 hỗ trợ tiếng Việt ổn định.");

        // Ghi xong thì đóng writer
        writer.close();


        //Đọc file

        BufferedReader reader = Files.newBufferedReader(
            file,
            StandardCharsets.UTF_8
        );

        int number = 1;

        String line;

        while ((line = reader.readLine()) != null) {

            System.out.println(number + ". " + line);

            number++;
        }

        // Đọc xong thì đóng reader
        reader.close();
    }
}