package moddedmite.rustedironcore.api.resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/** Reads current pack declarations while retaining their real format for format-sensitive consumers. */
public final class ModernPackMetadata {

    public static int packFormat(JsonObject pack) {
        JsonElement format = pack.has("pack_format") ? pack.get("pack_format") : pack.get("min_format");
        if (format == null) throw new JsonParseException("Missing pack_format or min_format");
        if (format.isJsonArray()) {
            if (format.getAsJsonArray().size() != 2) throw new JsonParseException("Expected [major, minor] pack format");
            format = format.getAsJsonArray().get(0);
        }
        try {
            int value = format.getAsInt();
            if (value >= 1) return value;
        } catch (RuntimeException failure) {
            throw new JsonParseException("Invalid resource-pack format", failure);
        }
        throw new JsonParseException("Resource-pack format must be positive");
    }

    public static JsonElement legacySection(JsonElement json) {
        JsonObject source = json.getAsJsonObject();
        boolean modernFormat = !source.has("pack_format") && source.has("min_format");
        JsonElement description = source.get("description");
        boolean component = description != null && (description.isJsonObject() || description.isJsonArray());
        if (!modernFormat && !component) return json;
        JsonObject copy = new JsonObject();
        for (var entry : source.entrySet()) copy.add(entry.getKey(), entry.getValue());
        if (modernFormat) copy.addProperty("pack_format", packFormat(source));
        if (component) copy.addProperty("description", text(description));
        return copy;
    }

    private static String text(JsonElement value) {
        if (value.isJsonPrimitive()) return value.getAsString();
        StringBuilder result = new StringBuilder();
        if (value.isJsonArray()) {
            for (JsonElement child : value.getAsJsonArray()) result.append(text(child));
        } else if (value.isJsonObject()) {
            JsonObject object = value.getAsJsonObject();
            if (object.has("text")) result.append(object.get("text").getAsString());
            else if (object.has("translate")) result.append(object.get("translate").getAsString());
            if (object.has("extra")) result.append(text(object.get("extra")));
        }
        return result.toString();
    }
}
