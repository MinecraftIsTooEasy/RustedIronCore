package moddedmite.rustedironcore.internal.compat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Detects which FishModLoader runs RIC.
 * <p>
 * FML 3.x renders through OpenGL, so RIC's render mixins target the original MITE method bodies (the legacy route).
 * FML 4.x with the Vulkan backend rewrites most render methods for its native renderer, one switchable "area" at a time
 * ({@code fml.vulkan.native}, {@code fml.vulkan.native.off}); RIC mixins on such methods take the native route instead.
 * An area whose native renderer is switched off runs MITE's code again and therefore uses the legacy route.
 */
public final class FmlRuntime {
    public enum Route {
        LEGACY, NATIVE
    }

    private static final Logger LOGGER = LogManager.getLogger("RustedIronCore");
    private static final String FML_CLASS = "net.xiaoyu233.fml.FishModLoader";
    private static final String VULKAN_BACKEND_CLASS = "net.xiaoyu233.fml.vulkan.VulkanBackend";
    private static final String VULKAN_CONFIG_CLASS = "net.xiaoyu233.fml.vulkan.VulkanConfig";

    private static final String VERSION = readVersion();
    private static final boolean VULKAN = majorVersion(VERSION) >= 4 && classPresent(VULKAN_BACKEND_CLASS);
    private static final Map<String, Boolean> NATIVE_AREAS = new ConcurrentHashMap<>();

    static {
        LOGGER.info("FishModLoader {} detected, {} render route", VERSION, VULKAN ? "Vulkan (per native area)" : "legacy OpenGL");
    }

    private FmlRuntime() {
    }

    /** Version of the running FishModLoader, "unknown" if it cannot be read. */
    public static String fmlVersion() {
        return VERSION;
    }

    /** True on FishModLoader 4.x with the Vulkan backend. */
    public static boolean isVulkan() {
        return VULKAN;
    }

    /** True when FML's native renderer owns the methods of {@code area} (names of {@code fishmodloader.native.<area>.mixins.json}). */
    public static boolean isNativeArea(String area) {
        return VULKAN && NATIVE_AREAS.computeIfAbsent(area, FmlRuntime::queryNativeArea);
    }

    public static Route route(String area) {
        return isNativeArea(area) ? Route.NATIVE : Route.LEGACY;
    }

    private static boolean queryNativeArea(String area) {
        boolean active;
        try {
            active = (Boolean) Class.forName(VULKAN_CONFIG_CLASS).getMethod("areaActive", String.class).invoke(null, area);
        } catch (ReflectiveOperationException | LinkageError | ClassCastException e) {
            // the native renderer is on by default in FML 4.x
            LOGGER.warn("Cannot query FML native area '{}', assuming it is active: {}", area, e.toString());
            active = true;
        }
        LOGGER.info("FML native area '{}': {} route", area, active ? "native" : "legacy");
        return active;
    }

    private static String readVersion() {
        // Reflection on purpose: FishModLoader.VERSION is a compile-time constant and would be inlined as the version RIC was built against
        try {
            return String.valueOf(Class.forName(FML_CLASS).getField("VERSION").get(null));
        } catch (ReflectiveOperationException | LinkageError e) {
            LOGGER.warn("Cannot read the FishModLoader version, using the legacy render route: {}", e.toString());
            return "unknown";
        }
    }

    static int majorVersion(String version) {
        int end = 0;
        while (end < version.length() && Character.isDigit(version.charAt(end))) {
            end++;
        }
        return end == 0 ? -1 : Integer.parseInt(version.substring(0, end));
    }

    private static boolean classPresent(String name) {
        try {
            Class.forName(name, false, FmlRuntime.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }
}
