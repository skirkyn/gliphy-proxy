package proxy.giphy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import proxy.giphy.server.ProxyServer;

import javax.annotation.PreDestroy;

@SpringBootApplication
public class GifSearchProxyApplication implements CommandLineRunner {

    @Autowired
    private ProxyServer proxyServer;

    public static void main(String[] args) {
        SpringApplication.run(GifSearchProxyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        proxyServer.start();
    }

    @PreDestroy
    public void stop() {
        proxyServer.stop();

    }
}
