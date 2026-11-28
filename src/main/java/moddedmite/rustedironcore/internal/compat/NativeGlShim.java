package moddedmite.rustedironcore.internal.compat;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * GL11-shaped state calls into FML 4.x's native renderer ({@code net.xiaoyu233.fml.vulkan.render.state.GlShim}), for RIC code on
 * the native route. RIC is built against FML 3.x, so the class is bound at runtime.
 * Only called on the native route, render thread only (like the shim itself).
 */
public final class NativeGlShim {
    private static final String GL_SHIM_CLASS = "net.xiaoyu233.fml.vulkan.render.state.GlShim";

    private static final MethodHandle ENABLE;
    private static final MethodHandle DISABLE;
    private static final MethodHandle BLEND_FUNC;
    private static final MethodHandle COLOR4F;
    private static final MethodHandle LINE_WIDTH;
    private static final MethodHandle DEPTH_MASK;

    static {
        MethodHandle enable = null, disable = null, blendFunc = null, color4f = null, lineWidth = null, depthMask = null;
        if (FmlRuntime.isVulkan()) {
            try {
                Class<?> shim = Class.forName(GL_SHIM_CLASS);
                MethodHandles.Lookup lookup = MethodHandles.publicLookup();
                enable = lookup.findStatic(shim, "enable", MethodType.methodType(void.class, int.class));
                disable = lookup.findStatic(shim, "disable", MethodType.methodType(void.class, int.class));
                blendFunc = lookup.findStatic(shim, "blendFunc", MethodType.methodType(void.class, int.class, int.class));
                color4f = lookup.findStatic(shim, "color4f", MethodType.methodType(void.class, float.class, float.class, float.class, float.class));
                lineWidth = lookup.findStatic(shim, "lineWidth", MethodType.methodType(void.class, float.class));
                depthMask = lookup.findStatic(shim, "depthMask", MethodType.methodType(void.class, boolean.class));
            } catch (ReflectiveOperationException | LinkageError e) {
                enable = null;
            }
        }
        ENABLE = enable;
        DISABLE = disable;
        BLEND_FUNC = blendFunc;
        COLOR4F = color4f;
        LINE_WIDTH = lineWidth;
        DEPTH_MASK = depthMask;
    }

    private NativeGlShim() {
    }

    public static void enable(int cap) {
        try {
            ENABLE.invokeExact(cap);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void disable(int cap) {
        try {
            DISABLE.invokeExact(cap);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void blendFunc(int src, int dst) {
        try {
            BLEND_FUNC.invokeExact(src, dst);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void color4f(float r, float g, float b, float a) {
        try {
            COLOR4F.invokeExact(r, g, b, a);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void lineWidth(float width) {
        try {
            LINE_WIDTH.invokeExact(width);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void depthMask(boolean flag) {
        try {
            DEPTH_MASK.invokeExact(flag);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    private static RuntimeException rethrow(Throwable t) {
        if (t instanceof RuntimeException runtime) throw runtime;
        if (t instanceof Error error) throw error;
        throw new IllegalStateException(t);
    }
}
