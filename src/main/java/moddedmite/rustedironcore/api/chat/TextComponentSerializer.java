package moddedmite.rustedironcore.api.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TextComponentSerializer {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static TextComponent fromJson(String json) {
        return fromJson(new JsonParser().parse(json));
    }

    public static TextComponent fromJson(JsonElement json) {
        if (json == null || json.isJsonNull()) {
            throw new JsonParseException("Text component cannot be null");
        }
        if (json.isJsonPrimitive()) {
            if (!json.getAsJsonPrimitive().isString()) {
                throw new JsonParseException("A primitive text component must be a string");
            }
            return TextComponent.literal(json.getAsString());
        }
        if (json.isJsonArray()) {
            JsonArray elements = json.getAsJsonArray();
            if (elements.size() == 0) throw new JsonParseException("Text component arrays cannot be empty");
            TextComponent root = fromJson(elements.get(0));
            for (int i = 1; i < elements.size(); i++) root.append(fromJson(elements.get(i)));
            return root;
        }
        if (!json.isJsonObject()) throw new JsonParseException("Expected string, array, or object component");

        JsonObject object = json.getAsJsonObject();
        TextComponent component = new TextComponent(readContent(object));
        component.setStyle(readStyle(object));

        JsonElement extra = object.get("extra");
        if (extra == null) extra = object.get("siblings");
        if (extra != null) {
            if (!extra.isJsonArray()) throw new JsonParseException("Component extra must be an array");
            for (JsonElement child : extra.getAsJsonArray()) component.append(fromJson(child));
        }

        // MITE's serializer used text arrays and "using" before it adopted the modern field names.
        JsonElement legacyTextChildren = object.get("text");
        if (legacyTextChildren != null && legacyTextChildren.isJsonArray()) {
            for (JsonElement child : legacyTextChildren.getAsJsonArray()) component.append(fromJson(child));
        }
        return component;
    }

    public static String toJson(TextComponent component) {
        return GSON.toJson(toJsonElement(component));
    }

    public static JsonElement toJsonElement(TextComponent component) {
        String collapsed = component.tryCollapseToString();
        if (collapsed != null) return new JsonPrimitive(collapsed);

        JsonObject object = new JsonObject();
        writeContent(object, component.getContent());
        writeStyle(object, component.getStyle());
        if (!component.getSiblings().isEmpty()) {
            JsonArray extra = new JsonArray();
            for (TextComponent sibling : component.getSiblings()) extra.add(toJsonElement(sibling));
            object.add("extra", extra);
        }
        return object;
    }

    private static TextComponent.Content readContent(JsonObject object) {
        JsonElement text = object.get("text");
        if (text != null && text.isJsonPrimitive() && text.getAsJsonPrimitive().isString()) {
            return new TextComponent.Literal(text.getAsString());
        }

        JsonElement translate = object.get("translate");
        if (translate != null) {
            List<Object> arguments = new ArrayList<>();
            JsonElement with = object.has("with") ? object.get("with") : object.get("using");
            if (with != null) {
                if (with.isJsonArray()) {
                    for (JsonElement argument : with.getAsJsonArray()) arguments.add(readArgument(argument));
                } else {
                    arguments.add(readArgument(with));
                }
            }
            return new TextComponent.Translatable(requireString(translate, "translate"), optionalString(object.get("fallback")), arguments);
        }

        JsonElement keybind = object.get("keybind");
        if (keybind != null) return new TextComponent.Keybind(requireString(keybind, "keybind"));

        JsonElement score = object.get("score");
        if (score != null) {
            if (!score.isJsonObject()) throw new JsonParseException("score must be an object");
            JsonObject scoreObject = score.getAsJsonObject();
            return new TextComponent.Score(requireString(scoreObject.get("name"), "score.name"),
                    requireString(scoreObject.get("objective"), "score.objective"));
        }

        JsonElement selector = object.get("selector");
        if (selector != null) {
            return new TextComponent.Selector(requireString(selector, "selector"), optionalComponent(object.get("separator")));
        }

        JsonElement nbt = object.get("nbt");
        if (nbt != null) {
            TextComponent.NbtSource source;
            JsonElement block = object.get("block");
            JsonElement entity = object.get("entity");
            JsonElement storage = object.get("storage");
            if (block != null) source = new TextComponent.BlockData(requireString(block, "block"));
            else if (entity != null) source = new TextComponent.EntityData(requireString(entity, "entity"));
            else if (storage != null) source = new TextComponent.StorageData(requireString(storage, "storage"));
            else throw new JsonParseException("nbt components require block, entity, or storage");
            JsonElement interpret = object.get("interpret");
            return new TextComponent.Nbt(requireString(nbt, "nbt"), interpret != null && interpret.getAsBoolean(),
                    optionalComponent(object.get("separator")), source);
        }

        if (text != null && text.isJsonArray()) return new TextComponent.Literal("");
        if (object.entrySet().isEmpty() || hasStyleFields(object)) return new TextComponent.Literal("");
        throw new JsonParseException("Object does not contain a recognized component content type");
    }

    private static Object readArgument(JsonElement argument) {
        if (argument.isJsonObject() || argument.isJsonArray()) return fromJson(argument);
        if (argument.isJsonPrimitive()) {
            JsonPrimitive primitive = argument.getAsJsonPrimitive();
            if (primitive.isString()) return primitive.getAsString();
            if (primitive.isNumber()) return primitive.getAsNumber();
            if (primitive.isBoolean()) return primitive.getAsBoolean();
        }
        throw new JsonParseException("Invalid translation argument: " + argument);
    }

    private static TextStyle readStyle(JsonObject object) {
        TextColor color = null;
        if (object.has("color") && !object.get("color").isJsonNull()) {
            color = TextColor.parse(requireString(object.get("color"), "color"));
            if (color == null) throw new JsonParseException("Invalid text color: " + object.get("color"));
        }
        JsonElement clickElement = object.has("clickEvent") ? object.get("clickEvent") : object.get("click_event");
        ChatClickEvent click = null;
        if (clickElement != null && !clickElement.isJsonNull()) {
            if (!clickElement.isJsonObject()) throw new JsonParseException("clickEvent must be an object");
            JsonObject clickObject = clickElement.getAsJsonObject();
            ChatClickAction action = ChatClickAction.fromSerializedName(requireString(clickObject.get("action"), "clickEvent.action"));
            if (action == null) throw new JsonParseException("Unknown click action: " + clickObject.get("action"));
            JsonElement value = clickObject.has("value") ? clickObject.get("value") : readLegacyClickValue(clickObject);
            click = new ChatClickEvent(action, requireString(value, "clickEvent.value"));
        }
        TextHoverEvent hover = readHoverEvent(object.get("hoverEvent"));
        return new TextStyle(color, optionalBoolean(object.get("bold")), optionalBoolean(object.get("italic")),
                optionalBoolean(object.has("underlined") ? object.get("underlined") : object.get("underline")),
                optionalBoolean(object.get("strikethrough")), optionalBoolean(object.get("obfuscated")),
                click, hover, optionalString(object.get("insertion")), optionalString(object.get("font")));
    }

    @Nullable
    private static TextHoverEvent readHoverEvent(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) return null;
        if (!element.isJsonObject()) throw new JsonParseException("hoverEvent must be an object");
        JsonObject object = element.getAsJsonObject();
        TextHoverEvent.Action action;
        try {
            action = TextHoverEvent.Action.fromSerializedName(requireString(object.get("action"), "hoverEvent.action"));
        } catch (IllegalArgumentException exception) {
            throw new JsonParseException(exception.getMessage(), exception);
        }
        JsonElement contents = object.has("contents") ? object.get("contents") : object.get("value");
        if (contents == null) throw new JsonParseException("hoverEvent requires contents");
        return new TextHoverEvent(action, contents);
    }

    private static void writeContent(JsonObject object, TextComponent.Content content) {
        if (content instanceof TextComponent.Literal literal) {
            object.addProperty("text", literal.text());
        } else if (content instanceof TextComponent.Translatable translatable) {
            object.addProperty("translate", translatable.key());
            if (translatable.fallback() != null) object.addProperty("fallback", translatable.fallback());
            if (!translatable.arguments().isEmpty()) {
                JsonArray arguments = new JsonArray();
                for (Object argument : translatable.arguments()) {
                    arguments.add(argument instanceof TextComponent component ? toJsonElement(component) : GSON.toJsonTree(argument));
                }
                object.add("with", arguments);
            }
        } else if (content instanceof TextComponent.Keybind keybind) {
            object.addProperty("keybind", keybind.name());
        } else if (content instanceof TextComponent.Score score) {
            JsonObject value = new JsonObject();
            value.addProperty("name", score.name());
            value.addProperty("objective", score.objective());
            object.add("score", value);
        } else if (content instanceof TextComponent.Selector selector) {
            object.addProperty("selector", selector.pattern());
            selector.separator().ifPresent(separator -> object.add("separator", toJsonElement(separator)));
        } else if (content instanceof TextComponent.Nbt nbt) {
            object.addProperty("nbt", nbt.path());
            object.addProperty("interpret", nbt.interpret());
            nbt.separator().ifPresent(separator -> object.add("separator", toJsonElement(separator)));
            if (nbt.source() instanceof TextComponent.BlockData block) object.addProperty("block", block.position());
            else if (nbt.source() instanceof TextComponent.EntityData entity) object.addProperty("entity", entity.selector());
            else if (nbt.source() instanceof TextComponent.StorageData storage) object.addProperty("storage", storage.id());
        }
    }

    private static void writeStyle(JsonObject object, TextStyle style) {
        if (style.getColor() != null) object.addProperty("color", style.getColor().serialize());
        addBoolean(object, "bold", style.getBold());
        addBoolean(object, "italic", style.getItalic());
        addBoolean(object, "underlined", style.getUnderlined());
        addBoolean(object, "strikethrough", style.getStrikethrough());
        addBoolean(object, "obfuscated", style.getObfuscated());
        if (style.getClickEvent() != null) {
            JsonObject click = new JsonObject();
            click.addProperty("action", style.getClickEvent().action().getSerializedName());
            click.addProperty("value", style.getClickEvent().value());
            object.add("clickEvent", click);
        }
        if (style.getHoverEvent() != null) {
            JsonObject hover = new JsonObject();
            hover.addProperty("action", style.getHoverEvent().action().getSerializedName());
            hover.add("contents", new JsonParser().parse(style.getHoverEvent().contents().toString()));
            object.add("hoverEvent", hover);
        }
        if (style.getInsertion() != null) object.addProperty("insertion", style.getInsertion());
        if (style.getFont() != null) object.addProperty("font", style.getFont());
    }

    private static void addBoolean(JsonObject object, String name, @Nullable Boolean value) {
        if (value != null) object.addProperty(name, value);
    }

    private static boolean hasStyleFields(JsonObject object) {
        return object.has("color") || object.has("bold") || object.has("italic") || object.has("underlined")
                || object.has("strikethrough") || object.has("obfuscated") || object.has("clickEvent")
                || object.has("click_event") || object.has("hoverEvent") || object.has("insertion") || object.has("font");
    }

    @Nullable
    private static JsonElement readLegacyClickValue(JsonObject object) {
        for (String key : List.of("url", "path", "command", "page")) {
            if (object.has(key)) return object.get(key);
        }
        return null;
    }

    private static Optional<TextComponent> optionalComponent(@Nullable JsonElement element) {
        return element == null || element.isJsonNull() ? Optional.empty() : Optional.of(fromJson(element));
    }

    @Nullable
    private static String optionalString(@Nullable JsonElement element) {
        return element == null || element.isJsonNull() ? null : requireString(element, "string value");
    }

    @Nullable
    private static Boolean optionalBoolean(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) return null;
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
            throw new JsonParseException("Expected boolean, got " + element);
        }
        return element.getAsBoolean();
    }

    private static String requireString(@Nullable JsonElement element, String field) {
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw new JsonParseException("Expected string for " + field);
        }
        return element.getAsString();
    }
}
