
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5002;

    private static final ConcurrentHashMap<String, ClientHandler> clients =
            new ConcurrentHashMap<>();

    private static final ExecutorService pool =
            Executors.newFixedThreadPool(20);

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat Server dang chay tai port " + PORT);

            while (true) {
                Socket socket = server.accept();
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void broadcast(String message, String sender) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equals(sender)) {
                entry.getValue().send(message);
            }
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(), StandardCharsets.UTF_8));

                out = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8), true);

                out.println("Nhap nickname:");

                while (true) {
                    String name = in.readLine();

                    if (name == null || name.equals("QUIT")) {
                        return;
                    }

                    name = name.trim();

                    if (name.isEmpty() || name.contains(" ")) {
                        out.println("ERR INVALID_NICKNAME");
                        continue;
                    }

                    if (clients.putIfAbsent(name, this) == null) {
                        nickname = name;
                        out.println("OK NICKNAME " + nickname);
                        System.out.println(nickname + " da ket noi.");
                        broadcast("SYSTEM " + nickname + " da tham gia.", nickname);
                        break;
                    } else {
                        out.println("ERR NICKNAME_TAKEN");
                    }
                }

                String request;

                while ((request = in.readLine()) != null) {
                    if (request.equals("QUIT")) {
                        out.println("BYE");
                        break;
                    }

                    if (request.equals("USERS")) {
                        List<String> names = new ArrayList<>(clients.keySet());
                        Collections.sort(names);
                        out.println("USERS " + String.join(", ", names));
                    } else if (request.startsWith("MSG ")) {
                        String content = request.substring(4);

                        if (content.isBlank()) {
                            out.println("ERR EMPTY_MESSAGE");
                            continue;
                        }

                        broadcast("MSG " + nickname + ": " + content, nickname);
                        out.println("OK SENT");
                        System.out.println(nickname + ": " + content);
                    } else {
                        out.println("ERR UNKNOWN_COMMAND");
                    }
                }

            } catch (IOException e) {
                System.out.println("Client ngat ket noi: " + e.getMessage());
            } finally {
                if (nickname != null && clients.remove(nickname, this)) {
                    System.out.println(nickname + " da ngat ket noi.");
                    broadcast("SYSTEM " + nickname + " da roi phong.", nickname);
                }

                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Loi dong socket: " + e.getMessage());
                }
            }
        }
    }
}
