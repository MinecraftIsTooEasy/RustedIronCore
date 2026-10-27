package moddedmite.rustedironcore.internal.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;

import java.util.Map;

public final class JsonComponentTranslations {

    public static void applyDocument(JsonObject document, Map<String, TextComponent> translations) {
        for (Map.Entry<String, JsonElement> entry : document.entrySet()) {
            if (!entry.getValue().isJsonArray()) {
                continue;
            }
            try {
                translations.put(entry.getKey(), TextComponentSerializer.fromJson(entry.getValue()));
            } catch (RuntimeException ignored) {
                // Vanilla keeps the previous component translation when a later value is not a component array.
            }
        }
    }
}
