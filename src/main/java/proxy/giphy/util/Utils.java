package proxy.giphy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public final class Utils {

    private Utils() {

    }

    public static void close(Closeable closeable, Logger log) {

        try {
            closeable.close();
        } catch (IOException ex) {
            log.error("can't close the closeable", ex);
        }

    }

    public static void handleSuppressedException(Throwable[] suppressed, Logger log) {
        if (suppressed != null) {
            for (Throwable th : suppressed) {
                log.error("suppressed exception during forwarding", th);
            }
        }
    }
}
