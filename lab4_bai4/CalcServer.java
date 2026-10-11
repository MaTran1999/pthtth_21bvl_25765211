
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class CalcServer {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(5001)) {
            System.out.println("Server dang chay tai port 5001...");
            System.out.println("Dang cho Client ket noi...");

            while (true) {
                try (Socket socket = server.accept();
                     BufferedReader in = new BufferedReader(
                         new InputStreamReader(
                             socket.getInputStream(), StandardCharsets.UTF_8));
                     PrintWriter out = new PrintWriter(
                         new OutputStreamWriter(
                             socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                    System.out.println("Client da ket noi!");

                    String request;

                    while ((request = in.readLine()) != null) {
                        System.out.println("Client gui: " + request);

                        if (request.equals("QUIT")) {
                            System.out.println("Client da thoat.");
                            break;
                        }

                        String response = calculate(request);
                        out.println(response);

                        System.out.println("Server tra ve: " + response);
                    }

                } catch (IOException e) {
                    System.out.println("Loi Client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    public static String calculate(String request) {
        String[] parts = request.trim().split("\\s+");

        if (parts.length != 4 || !parts[0].equals("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String operator = parts[1];

        if (!operator.equals("+") && !operator.equals("-") &&
            !operator.equals("*") && !operator.equals("/")) {
            return "ERR UNSUPPORTED_OPERATOR";
        }

        try {
            double a = Double.parseDouble(parts[2]);
            double b = Double.parseDouble(parts[3]);

            if (!Double.isFinite(a) || !Double.isFinite(b)) {
                return "ERR INVALID_NUMBER";
            }

            double result;

            switch (operator) {
                case "+":
                    result = a + b;
                    break;
                case "-":
                    result = a - b;
                    break;
                case "*":
                    result = a * b;
                    break;
                case "/":
                    if (b == 0) {
                        return "ERR DIVIDE_BY_ZERO";
                    }
                    result = a / b;
                    break;
                default:
                    return "ERR UNSUPPORTED_OPERATOR";
            }

            if (!Double.isFinite(result)) {
                return "ERR INVALID_NUMBER";
            }

            if (result == Math.rint(result) &&
                result >= Long.MIN_VALUE &&
                result <= Long.MAX_VALUE) {
                return "OK " + (long) result;
            }

            return "OK " + result;

        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }
    }
}
