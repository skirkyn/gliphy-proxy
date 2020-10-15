package proxy.giphy.types;

import java.util.Objects;

public class Request {
    private String method;
    private String host;
    private int port;
    private String protocolAndVersion;


    public Request(String method, String host, int port, String protocol) {
        this.method = method;
        this.host = host;
        this.port = port;
        this.protocolAndVersion = protocol;
    }

    public String getMethod() {
        return method;
    }


    public String getHost() {
        return host;
    }


    public int getPort() {
        return port;
    }


    public String getProtocolAndVersion() {
        return protocolAndVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return port == request.port &&
                method.equals(request.method) &&
                host.equals(request.host) &&
                protocolAndVersion.equals(request.protocolAndVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, host, port, protocolAndVersion);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", protocolAndVersion='" + protocolAndVersion + '\'' +
                '}';
    }
}
