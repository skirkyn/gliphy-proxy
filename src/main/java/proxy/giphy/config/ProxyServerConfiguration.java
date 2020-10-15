package proxy.giphy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proxy.giphy.ip.resolver.Ip4Resolver;
import proxy.giphy.ip.resolver.IpResolver;
import proxy.giphy.ip.validator.IpValidator;
import proxy.giphy.ip.validator.WhitelistIpValidator;
import proxy.giphy.request.parser.ConnectRequestParser;
import proxy.giphy.request.parser.RequestParser;
import proxy.giphy.server.GiphyProxyServer;
import proxy.giphy.server.ProxyServer;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ProxyServerConfiguration {
    @Bean
    public RequestParser requestParser(@Value("${proxy.giphy.request.parser.default.port:443}") int defaultPort, IpResolver resolver) {
        return new ConnectRequestParser(defaultPort, resolver);
    }

    @Bean
    public IpResolver resolver() {
        return new Ip4Resolver();
    }

    @Bean
    public IpValidator ipValidator(@Value("${proxy.giphy.request.ip.validator.whitelist}") List<String> whitelistedIps) {
        SortedSet<String> validIps = new TreeSet<>(whitelistedIps == null ? new LinkedList<>() : whitelistedIps);
        return new WhitelistIpValidator(validIps);
    }

    @Bean
    public ExecutorService threadPool(@Value("${proxy.giphy.server.pool.threads}") int threads) {
        return Executors.newFixedThreadPool(threads);

    }

    @Bean
    public ProxyServer proxyServer(@Value("${proxy.giphy.server.port}") int port,
                                   RequestParser requestParser,
                                   IpValidator validator,
                                   ExecutorService pool,
                                   @Value("${proxy.giphy.server.pool.shutdown.timeout.ms}") int shutdownTimeoutSecond) {

        return new GiphyProxyServer(port, requestParser, validator, pool, shutdownTimeoutSecond);
    }
}
