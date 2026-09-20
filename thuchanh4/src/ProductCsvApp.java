package thuchanh4.src;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProductCsvApp {

    public static void main(String[] args) {

        // Đường dẫn file CSV cần đọc
        Path input = Path.of("data", "products.csv");

        // Đường dẫn file báo cáo sẽ được tạo
        Path report = Path.of("data", "report.txt");

        // Danh sách dùng để chứa các sản phẩm đọc được
        List<Product> products = new ArrayList<>();


        // =========================
        // PHẦN 1: ĐỌC FILE CSV
        // =========================

        try (BufferedReader reader = Files.newBufferedReader(
                input, StandardCharsets.UTF_8)) {

            // Đọc và bỏ qua dòng tiêu đề:
            // ma,ten,donGia,soLuong
            reader.readLine();

            String line;
            int lineNumber = 1;

            // Đọc từng dòng cho đến hết file
            while ((line = reader.readLine()) != null) {

                lineNumber++;

                // Nếu dòng trống thì bỏ qua
                if (line.isBlank()) {
                    continue;
                }

                // Chia dòng thành 4 phần bằng dấu phẩy
                String[] parts = line.split(",", -1);

                // Nếu không đủ 4 phần thì bỏ qua
                if (parts.length != 4) {
                    System.err.println("Bỏ qua dòng " + lineNumber);
                    continue;
                }

                try {

                    // Tạo một Product từ dữ liệu CSV
                    Product product = new Product(
                        parts[0].trim(),
                        parts[1].trim(),
                        Double.parseDouble(parts[2].trim()),
                        Integer.parseInt(parts[3].trim())
                    );

                    // Thêm sản phẩm vào danh sách
                    products.add(product);

                } catch (IllegalArgumentException e) {

                    System.err.println(
                        "Dòng " + lineNumber
                        + " không hợp lệ: "
                        + e.getMessage()
                    );
                }
            }

        } catch (IOException e) {

            System.err.println(
                "Không đọc được CSV: " + e.getMessage()
            );

            return;
        }


        // =========================
        // PHẦN 2: IN SẢN PHẨM
        // VÀ TÍNH TỔNG GIÁ TRỊ
        // =========================

        double total = 0;

        for (Product product : products) {

            System.out.println(product);

            total = total + product.inventoryValue();
        }


        // =========================
        // PHẦN 3: TẠO report.txt
        // =========================

        try (BufferedWriter writer = Files.newBufferedWriter(
                report, StandardCharsets.UTF_8)) {

            writer.write(
                "Số sản phẩm: " + products.size()
            );

            writer.newLine();

            writer.write(
                "Tổng giá trị tồn kho: %,.0f VND".formatted(total)
            );

            writer.newLine();

        } catch (IOException e) {

            System.err.println(
                "Không ghi được báo cáo: " + e.getMessage()
            );
        }
    }
}