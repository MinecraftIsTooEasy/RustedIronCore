package moddedmite.rustedironcore.internal.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import moddedmite.rustedironcore.api.chat.ChatClickAction;
import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextColor;

import javax.annotation.Nullable;

public final class MessageComponentJsonCompatibility {

    public static JsonElement readProperty(JsonObject object, String name) {
        if ("using".equals(name)) {
            JsonElement legacyArguments = object.get("using");
            return legacyArguments == null ? object.get("with") : legacyArguments;
        }
        if ("color".equals(name)) {
            JsonElement color = object.get(name);
            if (color != null && color.isJsonPrimitive() && color.getAsJsonPrimitive().isString()) {
                TextColor parsedColor = TextColor.parse(color.getAsString());
                if (parsedColor != null && parsedColor.legacyName() == null) return new JsonPrimitive("white");
            }
            return color;
        }
        return object.get(name);
    }

    @Nullable
    public static ChatClickEvent readClickEvent(JsonObject object) {
        JsonElement clickEventElement = object.has("clickEvent")
                ? object.get("clickEvent")
                : object.get("click_event");
        if (clickEventElement == null || !clickEventElement.isJsonObject()) return null;

        JsonObject clickEvent = clickEventElement.getAsJsonObject();
        JsonElement actionElement = clickEvent.get("action");
        ChatClickAction action = actionElement != null && actionElement.isJsonPrimitive()
                ? ChatClickAction.fromSerializedName(actionElement.getAsString())
                : null;
        if (action == null) return null;

        JsonElement value = readClickValue(clickEvent);
        return value != null && value.isJsonPrimitive()
                ? new ChatClickEvent(action, value.getAsString())
                : null;
    }

    public static void writeLegacyFields(JsonObject object, ChatClickComponentAccess access) {
        JsonElement legacyArguments = object.remove("using");
        if (legacyArguments != null) object.add("with", legacyArguments);

        JsonElement legacyChildren = object.get("text");
        if (legacyChildren != null && legacyChildren.isJsonArray()) {
            object.remove("text");
            object.add("extra", legacyChildren);
        }
        if (!object.has("text") && !object.has("translate")) {
            object.addProperty("text", "");
        }

        ChatClickEvent event = access.ric$getClickEvent();
        if (event != null) {
            JsonObject clickEvent = new JsonObject();
            clickEvent.addProperty("action", event.action().getSerializedName());
            clickEvent.addProperty("value", event.value());
            object.add("clickEvent", clickEvent);
        }
        String insertion = access.ric$getInsertion();
        if (insertion != null) object.addProperty("insertion", insertion);
        String font = access.ric$getFont();
        if (font != null) object.addProperty("font", font);
    }

    private static JsonElement readClickValue(JsonObject clickEvent) {
        String[] names = {"value", "url", "path", "command", "page"};
        for (String name : names) {
            JsonElement value = clickEvent.get(name);
            if (value != null) return value;
        }
        return null;
    }
}
