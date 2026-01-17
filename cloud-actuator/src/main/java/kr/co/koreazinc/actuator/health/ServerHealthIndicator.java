package kr.co.koreazinc.actuator.health;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

public class ServerHealthIndicator implements HealthIndicator {

    private String host;

    private int port;

    private int timeout;

    public ServerHealthIndicator(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    public ServerHealthIndicator(String host, int port) {
        this(host, port, 1000);
    }

    @Override
    public Health health() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(this.host, this.port), timeout);
        } catch (IOException e) {
            return Health.down()
                .withDetail("endpoint", this.host + ":" + this.port)
                .withDetail("message", e.getMessage()).build();
        }
        return Health.up().withDetail("endpoint", this.host + ":" + this.port).build();
    }
}