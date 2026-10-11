
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BenchmarkClient {
    static final int COUNT = 1000;
    static final int RUNS = 5;
    static final int SIZE = 128;
    static final int TIMEOUT = 1000;

    static byte[] createMessage(int id) {
        byte[] data = new byte[SIZE];
        Arrays.fill(data, (byte) 'A');

        byte[] prefix = String.format("%04d:", id)
                .getBytes(StandardCharsets.UTF_8);

        System.arraycopy(prefix, 0, data, 0, prefix.length);
        return data;
    }

    static long[] testTCP() throws Exception {
        int received = 0;

        try (Socket socket = new Socket("127.0.0.1", 6000)) {
            socket.setSoTimeout(TIMEOUT);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            long start = System.nanoTime();

            for (int i = 0; i < COUNT; i++) {
                byte[] data = createMessage(i);

                out.write(data);
                out.flush();

                byte[] response = new byte[SIZE];
                int total = 0;

                while (total < SIZE) {
                    int n = in.read(response, total, SIZE - total);

                    if (n == -1) {
                        throw new EOFException("TCP disconnected");
                    }

                    total += n;
                }

                if (Arrays.equals(data, response)) {
                    received++;
                }
            }

            long end = System.nanoTime();

            return new long[]{end - start, received};
        }
    }

    static long[] testUDP() throws Exception {
        int received = 0;
        InetAddress address = InetAddress.getByName("127.0.0.1");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(address, 6001);
            socket.setSoTimeout(TIMEOUT);

            long start = System.nanoTime();

            for (int i = 0; i < COUNT; i++) {
                byte[] data = createMessage(i);

                DatagramPacket request = new DatagramPacket(
                    data, data.length, address, 6001
                );

                socket.send(request);

                byte[] buffer = new byte[SIZE];
                DatagramPacket response = new DatagramPacket(
                    buffer, buffer.length
                );

                try {
                    socket.receive(response);

                    if (response.getLength() == SIZE &&
                        Arrays.equals(data, response.getData())) {
                        received++;
                    }

                } catch (SocketTimeoutException e) {
                }
            }

            long end = System.nanoTime();

            return new long[]{end - start, received};
        }
    }

    public static void main(String[] args) {
        double tcpTotal = 0;
        double udpTotal = 0;
        int tcpReceived = 0;
        int udpReceived = 0;

        System.out.println("=== TCP vs UDP BENCHMARK ===");
        System.out.println("Environment: Java, Windows, localhost");
        System.out.println("Message size: " + SIZE + " bytes");
        System.out.println("Messages per test: " + COUNT);
        System.out.println("Timeout: " + TIMEOUT + " ms");
        System.out.println("Method: Send one, wait for echo");
        System.out.println();

        for (int i = 1; i <= RUNS; i++) {
            try {
                long[] tcp = testTCP();
                long[] udp = testUDP();

                double tcpMs = tcp[0] / 1_000_000.0;
                double udpMs = udp[0] / 1_000_000.0;

                tcpTotal += tcpMs;
                udpTotal += udpMs;

                tcpReceived += tcp[1];
                udpReceived += udp[1];

                System.out.println("=== RUN " + i + " ===");
                System.out.printf(
                    "TCP: %.3f ms | Received: %d/%d%n",
                    tcpMs, tcp[1], COUNT
                );
                System.out.printf(
                    "UDP: %.3f ms | Received: %d/%d%n",
                    udpMs, udp[1], COUNT
                );
                System.out.println();

            } catch (Exception e) {
                System.out.println("Run " + i +
                    " failed: " + e.getMessage());
                return;
            }
        }

        System.out.println("=== AVERAGE RESULTS ===");

        System.out.printf("TCP Average: %.3f ms%n",
            tcpTotal / RUNS);

        System.out.printf("UDP Average: %.3f ms%n",
            udpTotal / RUNS);

        System.out.println("TCP Total Received: " +
            tcpReceived + "/" + (COUNT * RUNS));

        System.out.println("UDP Total Received: " +
            udpReceived + "/" + (COUNT * RUNS));
    }
}
