package proxy.giphy.ip.validator;

import java.util.SortedSet;

public class WhitelistIpValidator implements IpValidator {
    private SortedSet<String> whitelistedIps;

    public WhitelistIpValidator(SortedSet<String> whitelistedIps) {
        this.whitelistedIps = whitelistedIps;
    }

    @Override
    public boolean isValid(String ip) {
        return whitelistedIps.contains(ip);
    }
}

