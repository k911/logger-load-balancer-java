package ServerLoadBalancer.src.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketConnectionFactory {

    private String host;
    private int port;

    public SocketConnectionFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Initialize factory via data from environment variables
     *
     * @param hostEnv environment variable name
     * @param portEnv environment variable name
     */
    public SocketConnectionFactory(String hostEnv, String portEnv) {
        int port = Integer.parseInt(System.getenv(portEnv));
        String host = System.getenv(hostEnv);

        if (null == host) {
            throw new RuntimeException("Environment variable `" + hostEnv + "` is not set.");
        }

        this.port = port;
        this.host = host;
    }

    public Socket make() {
        try {
            return new Socket(InetAddress.getByName(host), port);
        } catch (IOException e) {
            System.err.println("Unknown host: " + host + ":" + port);
            return null;
        }
    }
}
