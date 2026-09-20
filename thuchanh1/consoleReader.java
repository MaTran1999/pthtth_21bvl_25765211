import java.io.BufferedReader;
import java.io.InputStreamReader;

public class consoleReader {
    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        int count = 0;

        System.out.println("Nhập văn bản, ấn Q để kết thúc");

        while(true) {
            String line = reader.readLine();

            if(line.equals("q")) {
                break;
            }
            count++;

            System.out.println("Dòng "+count+":"+line);
        }
        System.out.println("Tổng số dòng của bạn là: "+count);
    }
}