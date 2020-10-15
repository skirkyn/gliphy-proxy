package proxy.giphy.ip.resolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ip4Resolver implements IpResolver {

    private static final String IP_PATTERN = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

    @Override
    public String resolve(String hostname) {
        if (IP_PATTERN.matches(hostname)) {
            return hostname;
        }
        try {
            InetAddress address = InetAddress.getByName(hostname);
            return address.getHostAddress();

        } catch (UnknownHostException ex) {
            throw new RuntimeException("can't resolve ip address", ex);
        }

    }
}
