package proxy.giphy.request.handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(JUnit4.class)
public class GiphyRequestHandlerTest {

    @Test
    public void shouldMatchValuesInRequest(){
        Pattern pat = Pattern.compile("(\\w+)\\s+([\\w|\\d|.]+):?([\\d]*)\\s+(\\w+)/([\\d|.]+)");// GiphyRequestHandler.PATTERN;
        Matcher matcher1= pat.matcher("CONNECT 12.134.23.1:45 HTTP/1.1");

        int counter = 1;
        if (matcher1.find()){

            System.out.println(matcher1.group(counter++));
            System.out.println(matcher1.group(counter++));
            System.out.println(matcher1.group(counter++));
            System.out.println(matcher1.group(counter++));
            System.out.println(matcher1.group(counter++));

        }
    }
}
