package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import huix.glacier.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonItemModelOverride {

    private final Identifier modelId;
    private final Map<Identifier, Float> predicates;

    private JsonItemModelOverride(Identifier modelId, Map<Identifier, Float> predicates) {
        this.modelId = modelId;
        this.predicates = predicates;
    }

    public static JsonItemModelOverride fromJson(JsonObject object) {
        if (!object.has("model") || !object.get("model").isJsonPrimitive()
                || !object.has("predicate") || !object.get("predicate").isJsonObject()) {
            return null;
        }

        Identifier modelId = new Identifier(object.get("model").getAsString());
        Map<Identifier, Float> predicates = new LinkedHashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.getAsJsonObject("predicate").entrySet()) {
            if (entry.getValue().isJsonPrimitive()) {
                predicates.put(new Identifier(entry.getKey()), entry.getValue().getAsFloat());
            }
        }
        return predicates.isEmpty() ? null : new JsonItemModelOverride(modelId, predicates);
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    public Map<Identifier, Float> getPredicates() {
        return this.predicates;
    }
}
