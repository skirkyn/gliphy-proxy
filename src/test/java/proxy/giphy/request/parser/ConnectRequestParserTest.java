package proxy.giphy.request.parser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import proxy.giphy.ip.resolver.IpResolver;
import proxy.giphy.types.Request;


import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ConnectRequestParserTest {

    private String firstLine;
    private Request request;
    private ConnectRequestParser parser;

    @Before
    public void before(){
        IpResolver resolver = Mockito.mock(IpResolver.class);
        Mockito.when(resolver.resolve(Mockito.anyString())).then(AdditionalAnswers.returnsFirstArg());
        this.parser = new ConnectRequestParser(443, resolver);
    }

    public ConnectRequestParserTest(String firstLine, Request request) {
        this.firstLine = firstLine;
        this.request = request;
    }

    @Test
    public void shouldParseWithHostNameAndPort() {
        Assert.assertEquals(request, parser.parse(firstLine));
    }

    @Parameterized.Parameters
    public static Collection getParameters() {
        return Arrays.asList(new Object[][]{
                {"CONNECT www.test.com:443 HTTP/1.1", new Request("CONNECT", "www.test.com", 443, "HTTP/1.1")},
                {"CONNECT test.com:443 HTTP/1.1", new Request("CONNECT", "test.com", 443, "HTTP/1.1")},
                {"CONNECT www.test.com HTTP/1.1", new Request("CONNECT", "www.test.com", 443, "HTTP/1.1")},
                {"CONNECT 1.23.34.56:443 HTTP/1.1", new Request("CONNECT", "1.23.34.56", 443, "HTTP/1.1")},
                {"CONNECT 1.23.34.56 HTTP/1.1", new Request("CONNECT", "1.23.34.56", 443, "HTTP/1.1")},
                {"GET 1.23.34.56 HTTP/1.1", null},

        });
    }
}
