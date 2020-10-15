package proxy.giphy.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.giphy.ip.validator.IpValidator;
import proxy.giphy.request.handler.GiphyRequestHandler;
import proxy.giphy.request.parser.RequestParser;
import proxy.giphy.util.Utils;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GiphyProxyServer implements ProxyServer {

    private static Logger LOG = LoggerFactory.getLogger(GiphyProxyServer.class);

    private int port;
    private IpValidator validator;
    private RequestParser requestParser;
    private volatile boolean continueRunning;
    private Lock lock;
    private ExecutorService pool;
    private int shutdownTimeoutMs;

    public GiphyProxyServer(int port,
                            RequestParser requestParser,
                            IpValidator validator,
                            ExecutorService pool,
                            int shutdownTimeoutSeconds) {
        this.port = port;
        this.requestParser = requestParser;
        this.validator = validator;
        this.lock = new ReentrantLock();
        this.pool = pool;
        this.shutdownTimeoutMs = shutdownTimeoutSeconds;
    }

    public void start() {
        LOG.warn("starting the server");
        lock.lock();
        this.continueRunning = true;
        lock.unlock();
        try (ServerSocket socket = new ServerSocket(port)) {
            LOG.warn("server started");
            while (continueRunning) {
                try {
                    Socket clientSocket = socket.accept();
                    pool.submit(() -> new GiphyRequestHandler(requestParser, validator).handle(clientSocket));

                } catch (IOException ex) {
                    LOG.error("can't accept the connection", ex);
                }
            }


        } catch (IOException ex) {
            LOG.error("can't start proxy server", ex);
            Utils.handleSuppressedException(ex.getSuppressed(), LOG);
            throw new RuntimeException(ex);
        }

    }

    public void stop() {

        LOG.warn("shutting down the server");
        lock.lock();
        this.continueRunning = false;
        lock.unlock();

        try {
            pool.shutdown();
            pool.awaitTermination(this.shutdownTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            LOG.error("error while waiting for the tasks to complete", ex);
        }
        LOG.warn("server has been stopped");
    }
}
