package moddedmite.rustedironcore.client.api.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Registers providers that add click targets to otherwise unstructured chat text.
 */
public final class ChatClickTextProviders {

    private static final CopyOnWriteArrayList<ChatClickTextProvider> PROVIDERS = new CopyOnWriteArrayList<>();

    public static void register(ChatClickTextProvider provider) {
        PROVIDERS.addIfAbsent(Objects.requireNonNull(provider, "provider"));
    }

    public static List<ChatClickTextRange> findClickTargets(String plainText) {
        List<ChatClickTextRange> ranges = new ArrayList<>();
        for (ChatClickTextProvider provider : PROVIDERS) {
            for (ChatClickTextRange range : provider.findClickTargets(plainText)) {
                if (range.start() < range.end() && range.end() <= plainText.length()) {
                    ranges.add(range);
                }
            }
        }
        return List.copyOf(ranges);
    }
}
