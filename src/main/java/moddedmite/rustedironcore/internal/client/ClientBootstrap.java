package moddedmite.rustedironcore.internal.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientBootstrap {
    public static final Logger LOGGER = LogManager.getLogger(ClientBootstrap.class);

    // nothing to bootstrap now
    public static void bootstrap() {
//        Instant start = Instant.now();
//        LOGGER.info("client bootstrap started");
//
//        LOGGER.info("client bootstrap finished in {} millis", Duration.between(start, Instant.now()).toMillis());
    }

    public static void logStage(String stage) {
        LOGGER.info("{} bootstrap done", stage);
    }

    public static void throwStage(String stage) {
        throw new IllegalStateException(stage + " registry failed");
    }
}
