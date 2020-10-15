package proxy.giphy;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import proxy.giphy.config.ProxyServerConfiguration;
import proxy.giphy.server.ProxyServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProxyServerConfiguration.class)
@ActiveProfiles("test")
public class GifSearchProxyApplicationTest {

    @Value("${proxy.giphy.server.port}")
    private int port;

    @Value("${proxy.giphy.server.pool.shutdown.timeout.ms}")
    private int timeout;

    @Autowired
    private ProxyServer server;

    private OkHttpClient client;

    private static final String validUrl = "https://api.giphy.com/v1/gifs/search?api_key=3o6ZsYH6U6Eri53TXy&offset=0&limit=5&q=dog";

    private static final String invalidUrl = "https://www.devglan.com/online-tools/jasypt-online-encryption-decryption";

    @Before
    public void before() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", port));

        client = new OkHttpClient.Builder().proxy(proxy).build();
        CompletableFuture.runAsync(() -> server.start());

    }

    @After
    public void after() {
        client = null;
        server.stop();
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            //
        }

    }


    @Test
    public void shouldForwardTheRequestForWhitelistedIp() throws Exception {
        Request request = new Request.Builder().url(validUrl).build();
        Response response = client.newCall(request).execute();
        Assert.assertTrue(response.isSuccessful());
        Assert.assertTrue(StringUtils.isNotBlank(response.body().string()));

    }

    @Test(expected = IOException.class)
    public void shouldNotForwardTheRequestForIpNotInTheWhiteList() throws Exception {
        Request request = new Request.Builder().url(invalidUrl).build();
        Response response = client.newCall(request).execute();
        Assert.assertFalse(response.isSuccessful());

    }

}
