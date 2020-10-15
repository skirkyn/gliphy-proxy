package proxy.giphy.request.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import proxy.giphy.ip.validator.IpValidator;
import proxy.giphy.request.parser.RequestParser;
import proxy.giphy.types.Request;
import proxy.giphy.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;

public class GiphyRequestHandler implements Handler {

    private static Logger LOG = LoggerFactory.getLogger(GiphyRequestHandler.class);

    private RequestParser requestParser;
    private IpValidator ipValidator;
    private static int BUFFER = 400;


    public GiphyRequestHandler(RequestParser requestParser, IpValidator ipValidator) {
        this.requestParser = requestParser;
        this.ipValidator = ipValidator;
    }


    @Override
    public void handle(Socket socket) {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String firstLine = in.readLine();

            if (StringUtils.isBlank(firstLine)) {
                LOG.error("can't process request, first line is empty");
                throw new RuntimeException("first line is empty");
            }
            Request request = requestParser.parse(firstLine);

            if (request == null) {
                LOG.error("can't parse request " + firstLine);
                throw new RuntimeException("invalid request ");
            }

            if (!ipValidator.isValid(request.getHost())) {

                try (OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset())) {
                    out.write(String.format("%s %d %s", request.getProtocolAndVersion(), HttpStatus.PROXY_AUTHENTICATION_REQUIRED.value(),
                            HttpStatus.PROXY_AUTHENTICATION_REQUIRED.getReasonPhrase()));
                    out.write("Proxy-agent: Giphy/0.1\r\n");
                    out.write("\r\n");
                    out.flush();
                } catch (Exception ex) {
                    LOG.error("error responding to the invalid redirect", ex);
                }
                return;
            }
            skipHeaders(in);

            respondToConnect(request, new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset()));

            forward(socket, new Socket(request.getHost(), request.getPort()));

        } catch (IOException ex) {
            LOG.error("can't read from the socket", ex);
            Utils.handleSuppressedException(ex.getSuppressed(), LOG);
        } finally {
            if (!socket.isClosed()) {
                Utils.close(socket, LOG);
            }
        }


    }

    private void skipHeaders(BufferedReader in) throws IOException {
        String line = in.readLine();
        while (StringUtils.isNotBlank(line)) {
            line = in.readLine();
        }
    }


    private void forward(Socket clientSocket, Socket serverSocket) {

        Tunnel clToSrv = new Tunnel(clientSocket, serverSocket, BUFFER);
        Tunnel srvToCl = new Tunnel(serverSocket, clientSocket, BUFFER);
        CompletableFuture<Void> clToSrvFuture = CompletableFuture.runAsync(clToSrv);
        CompletableFuture<Void> srvToClFuture = CompletableFuture.runAsync(srvToCl);
        clToSrvFuture.join();
        srvToClFuture.join();
    }

    private void respondToConnect(Request request, OutputStreamWriter out) {
        try {
            out.write(String.format("%s %d %s", request.getProtocolAndVersion(), HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase()));
            out.write("Proxy-agent: Giphy/0.1\r\n");
            out.write("\r\n");
            out.flush();

        } catch (IOException ex) {
            LOG.error("can't establish connection", ex);
            throw new RuntimeException(ex);
        }
    }

    private static class Tunnel implements Runnable {

        private Socket sockIn;
        private Socket sockOut;
        private int bufferSize;

        public Tunnel(Socket sockIn, Socket sockOut, int bufferSize) {
            this.sockIn = sockIn;
            this.sockOut = sockOut;
            this.bufferSize = bufferSize;

        }

        public void run() {

            try (InputStream input = sockIn.getInputStream();
                 OutputStream output = sockOut.getOutputStream()) {
                byte[] buf = new byte[this.bufferSize];
                int bytesRead = 0;

                try {
                    while ((bytesRead = input.read(buf)) >= 0) {
                        output.write(buf, 0, bytesRead);
                        output.flush();
                    }
                } catch (IOException e) {

                    Utils.close(input, LOG);
                    Utils.close(output, LOG);

                }
            } catch (IOException ex) {
                LOG.error("exception turing execution ", ex);
                Utils.handleSuppressedException(ex.getSuppressed(), LOG);
            }


        }

    }
}
