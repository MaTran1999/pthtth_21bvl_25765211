package network;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspector {

    public static void main(String[] args) {

        // Phải có đúng 2 tham số: hostname và URI
        if (args.length != 2) {
            System.out.println(
                "Usage: java network.HostUriInspector <hostname> <uri>"
            );
            return;
        }

        String hostname = args[0];
        String uriText = args[1];

        // =========================
        // PHẦN 1: HOST INSPECTOR
        // =========================
        System.out.println("=== HOST INSPECTOR ===");
        System.out.println("Hostname: " + hostname);

        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);

            for (InetAddress address : addresses) {

                String ip = address.getHostAddress();

                System.out.println("IP: " + ip);

                // IPv6 có dấu :
                if (ip.contains(":")) {
                    System.out.println("Type: IPv6");
                } else {
                    System.out.println("Type: IPv4");
                }

                System.out.println(
                    "Loopback: " + address.isLoopbackAddress()
                );

                System.out.println(
                    "Site local: " + address.isSiteLocalAddress()
                );

                System.out.println("--------------------");
            }

        } catch (UnknownHostException e) {
            System.out.println(
                "ERROR: Khong phan giai duoc hostname: " + hostname
            );
        }

        // =========================
        // PHẦN 2: URI INSPECTOR
        // =========================
        System.out.println();
        System.out.println("=== URI INSPECTOR ===");

        try {
            URI uri = new URI(uriText);

            System.out.println("URI: " + uriText);
            System.out.println("Scheme: " + uri.getScheme());
            System.out.println("Host: " + uri.getHost());
            System.out.println("Port: " + uri.getPort());
            System.out.println("Path: " + uri.getPath());
            System.out.println("Query: " + uri.getQuery());
            System.out.println("Fragment: " + uri.getFragment());

        } catch (URISyntaxException e) {
            System.out.println(
                "ERROR: URI khong hop le: " + uriText
            );
        }
    }
}