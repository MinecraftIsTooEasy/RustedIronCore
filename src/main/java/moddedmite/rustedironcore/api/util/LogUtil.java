package moddedmite.rustedironcore.api.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.LoggerContext;

import java.util.function.Supplier;

public class LogUtil {
    public static final String FATAL_MARKER_ID = "FATAL";
    public static final Marker FATAL_MARKER = MarkerManager.getMarker(FATAL_MARKER_ID);
    private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    public static boolean isLoggerActive() {
        final LoggerContext loggerContext = LogManager.getContext();
        if (loggerContext instanceof LifeCycle lifeCycle) {
            return !lifeCycle.isStopped();
        }
        return true; // Sensible default? In worst case, no logs - so not a huge loss
    }

    public static void configureRootLoggingLevel(final org.apache.logging.log4j.Level level) {
        final org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public static Object defer(final Supplier<Object> result) {
        class ToString {
            @Override
            public String toString() {
                return result.get().toString();
            }
        }

        return new ToString();
    }

    /**
     * Caller sensitive, DO NOT WRAP
     */
    public static Logger getLogger() {
        return LogManager.getLogger(STACK_WALKER.getCallerClass());
    }
}
