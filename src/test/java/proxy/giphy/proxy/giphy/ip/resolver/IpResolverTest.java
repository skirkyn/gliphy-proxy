package proxy.giphy.proxy.giphy.ip.resolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import proxy.giphy.ip.resolver.Ip4Resolver;
import proxy.giphy.ip.resolver.IpResolver;

@RunWith(JUnit4.class)
public class IpResolverTest {

    private IpResolver resolver;

    @Before
    public void before(){
        this.resolver = new Ip4Resolver();
    }
    @Test
    public void shouldResolveLocalhost(){
        Assert.assertEquals("127.0.0.1", resolver.resolve("localhost"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldNotResolveUnknownHost(){
        Assert.assertEquals("127.0.0.1", resolver.resolve("someHost"));

    }

    @Test
    public void shouldNorResolveIp(){
        Assert.assertEquals("127.0.0.1", resolver.resolve("127.0.0.1"));
    }

}
