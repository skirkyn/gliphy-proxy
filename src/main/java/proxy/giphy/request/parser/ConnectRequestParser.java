package proxy.giphy.request.parser;

import org.apache.commons.lang3.StringUtils;
import proxy.giphy.ip.resolver.IpResolver;
import proxy.giphy.types.Request;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectRequestParser implements RequestParser {
    private static final Pattern PATTERN = Pattern.compile("(CONNECT)\\s+([\\w|\\d|.]+):?([\\d]*)\\s+(\\w+/[\\d|.]+)");
    private static final int FIRST = 1;

    private int defaultPort;

    public ConnectRequestParser(int defaultPort, IpResolver ipResolver) {
        this.defaultPort = defaultPort;
        this.ipResolver = ipResolver;
    }

    private IpResolver ipResolver;


    @Override
    public Request parse(String firstLine) {
        Matcher matcher = PATTERN.matcher(firstLine);
        if (matcher.find()) {
            int counter = FIRST;
            String method = matcher.group(counter++);
            if (method == null) {
                return null;
            }
            String host = matcher.group(counter++);
            String port = matcher.group(counter++);
            String protocol = matcher.group(counter);
            return new Request(method, ipResolver.resolve(host), StringUtils.isBlank(port) ? defaultPort : Integer.parseInt(port), protocol);
        }
        return null;
    }
}
