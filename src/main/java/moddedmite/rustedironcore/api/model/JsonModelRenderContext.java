package moddedmite.rustedironcore.api.model;

import java.util.ArrayDeque;
import java.util.Deque;

public final class JsonModelRenderContext {

    private static final ThreadLocal<Deque<JsonBlockModel.RenderContext>> CONTEXTS = ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(JsonBlockModel.RenderContext context) {
        CONTEXTS.get().push(context);
    }

    public static void pop() {
        Deque<JsonBlockModel.RenderContext> contexts = CONTEXTS.get();
        if (!contexts.isEmpty()) {
            contexts.pop();
        }
        if (contexts.isEmpty()) {
            CONTEXTS.remove();
        }
    }

    public static JsonBlockModel.RenderContext currentOr(JsonBlockModel.RenderContext fallback) {
        Deque<JsonBlockModel.RenderContext> contexts = CONTEXTS.get();
        return contexts.isEmpty() ? fallback : contexts.peek();
    }
}
