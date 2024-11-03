package huix.glacier.util;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

public class GlacierLog {
    public static final GlacierLog instance = new GlacierLog();

    public static final LogCategory category = LogCategory.create("glacier-api");


    public static void error(String format) {
        Log.error(category, format);
    }
    public static void error(String format, Throwable exc) {
        Log.error(category, format, exc);
    }
    public static void error(String format, String msg, Throwable exc) {
        Log.error(category, format, exc, msg);
    }
    public static void error(String format, Object... args) {
        Log.error(category, format, args);
    }

    public static void warn(String format) {
        Log.warn(category, format);
    }
    public static void warn(String format, Throwable exc) {
        Log.warn(category, format, exc);
    }
    public static void warn(String format, String msg, Throwable exc) {
        Log.warn(category, format, exc, msg);
    }
    public static void warn(String format, Object... args) {
        Log.warn(category, format, args);
    }

    public static void info(String format) {
        Log.info(category, format);
    }
    public static void info(String format, Throwable exc) {
        Log.info(category, format, exc);
    }
    public static void info(String format, String msg, Throwable exc) {
        Log.info(category, format, exc, msg);
    }
    public static void info(String format, Object... args) {
        Log.info(category, format, args);
    }

    public static void debug(String format) {
        Log.debug(category, format);
    }
    public static void debug(String format, Throwable exc) {
        Log.debug(category, format, exc);
    }
    public static void debug(String format, String msg, Throwable exc) {
        Log.debug(category, format, exc, msg);
    }
    public static void debug(String format, Object... args) {
        Log.debug(category, format, args);
    }

    public static void trace(String format) {
        Log.trace(category, format);
    }
}
