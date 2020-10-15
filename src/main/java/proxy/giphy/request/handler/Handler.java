package proxy.giphy.request.handler;

import java.net.Socket;

public interface Handler {

    void handle(Socket socket);

}
