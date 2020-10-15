package proxy.giphy.ip.validator;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class WhitelistIpValidatorTest {
    private IpValidator ipValidator;
    private String ip;
    private boolean validationResult;

    public WhitelistIpValidatorTest(List<String> whitelistedIps, String ip, boolean validationResult) {
        this.ipValidator = new WhitelistIpValidator(new TreeSet<>(whitelistedIps));
        this.validationResult = validationResult;
        this.ip = ip;
    }

    @Test
    public void shouldValidateIps(){
        Assert.assertEquals(validationResult, ipValidator.isValid(ip));
    }

    @Parameterized.Parameters
    public static Collection getParameters() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList("1.23.12.1"), "1.23.12.1", true},
                {Arrays.asList("1.23.12.1"), "1.23.12.2", false},
                {new LinkedList<>(), "1.23.12.1", false}
        });
    }
}
