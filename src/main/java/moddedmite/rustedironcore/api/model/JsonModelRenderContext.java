package moddedmite.rustedironcore.api.model;

import java.util.ArrayDeque;
import java.util.Deque;

public final class JsonModelRenderContext {

    private static final ThreadLocal<Deque<JsonModelDisplayContext>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(JsonModelDisplayContext context) {
        CONTEXTS.get().push(context);
    }

    public static void pop() {
        Deque<JsonModelDisplayContext> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    public static JsonModelDisplayContext currentOr(JsonModelDisplayContext fallback) {
        Deque<JsonModelDisplayContext> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? fallback : contexts.peek();
    }
}
