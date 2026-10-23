package moddedmite.rustedironcore.api.model;

import java.util.ArrayDeque;
import java.util.Deque;

public final class JsonModelRenderContext {

    private record Frame(JsonModelDisplayContext context, boolean modernPrefix) {}

    private static final ThreadLocal<Deque<Frame>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);
    private static final ThreadLocal<Float> FRAME_TIME = ThreadLocal.withInitial(() -> 0f);

    /** Set before world rendering, so item selection uses this frame rather than the GUI tick. */
    public static void beginFrame(float partialTick) { FRAME_TIME.set(partialTick); }

    public static float partialTick() { return FRAME_TIME.get(); }

    public static void push(JsonModelDisplayContext context) {
        CONTEXTS.get().push(new Frame(context, false));
    }

    /** The caller has already supplied the modern hand prefix; do not undo a legacy suffix. */
    public static void pushModern(JsonModelDisplayContext context) {
        CONTEXTS.get().push(new Frame(context, true));
    }

    public static boolean hasModernPrefix() {
        Deque<Frame> contexts = CONTEXTS.get();
        return !contexts.isEmpty() && contexts.peek().modernPrefix();
    }

    public static void pop() {
        Deque<Frame> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    public static JsonModelDisplayContext currentOr(JsonModelDisplayContext fallback) {
        Deque<Frame> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? fallback : contexts.peek().context();
    }
}
