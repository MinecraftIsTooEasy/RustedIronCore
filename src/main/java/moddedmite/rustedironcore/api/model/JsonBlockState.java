package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import huix.glacier.util.Identifier;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public final class JsonBlockState {

    public static final Variant EMPTY_VARIANT = new Variant(null, 0, 0, false);

    private final Identifier sourceId;
    private final Map<String, Variant> variants = new HashMap<>();

    private JsonBlockState(Identifier sourceId) {
        this.sourceId = sourceId;
    }

    public static JsonBlockState fromJson(Identifier sourceId, Reader reader) {
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        JsonBlockState blockState = new JsonBlockState(sourceId);

        JsonObject variants = root.has("variants") && root.get("variants").isJsonObject() ? root.getAsJsonObject("variants") : root;
        for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
            Variant variant = parseVariant(entry.getValue());
            if (variant.getModelId() != null) {
                blockState.variants.put(entry.getKey(), variant);
            }
        }

        return blockState;
    }

    private static Variant parseVariant(JsonElement value) {
        if (value.isJsonObject()) {
            return Variant.fromJson(value.getAsJsonObject());
        }

        if (value.isJsonArray()) {
            JsonArray array = value.getAsJsonArray();
            if (array.size() > 0 && array.get(0).isJsonObject()) {
                return Variant.fromJson(array.get(0).getAsJsonObject());
            }
        }

        return EMPTY_VARIANT;
    }

    public Variant getVariant(int metadata) {
        String metadataKey = Integer.toString(metadata);
        Variant variant = this.variants.get(metadataKey);
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("metadata=" + metadata);
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("meta=" + metadata);
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("damage=" + metadata);
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("normal");
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("default");
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get("");
        if (variant != null) {
            return variant;
        }

        variant = this.variants.get(this.sourceId.getPath());
        if (variant != null) {
            return variant;
        }

        String facing = getFacingKey(metadata);
        if (facing != null) {
            variant = this.variants.get(facing);
            if (variant != null) {
                return variant;
            }
        }
        return this.variants.size() == 1 ? this.variants.values().iterator().next() : EMPTY_VARIANT;
    }

    private static String getFacingKey(int metadata) {
        return switch (metadata & 3) {
            case 0 -> "facing=south";
            case 1 -> "facing=west";
            case 2 -> "facing=north";
            case 3 -> "facing=east";
            default -> null;
        };
    }

    public static final class Variant {
        private final Identifier modelId;
        private final int x;
        private final int y;
        private final boolean uvLock;

        private Variant(Identifier modelId, int x, int y, boolean uvLock) {
            this.modelId = modelId;
            this.x = normalizeRotation(x);
            this.y = normalizeRotation(y);
            this.uvLock = uvLock;
        }

        public static Variant simple(Identifier modelId) {
            return new Variant(modelId, 0, 0, false);
        }

        private static Variant fromJson(JsonObject object) {
            if (!object.has("model")) {
                return EMPTY_VARIANT;
            }

            Identifier modelId = new Identifier(object.get("model").getAsString());
            int x = object.has("x") ? object.get("x").getAsInt() : 0;
            int y = object.has("y") ? object.get("y").getAsInt() : 0;
            boolean uvLock = object.has("uvlock") && object.get("uvlock").getAsBoolean();
            return new Variant(modelId, x, y, uvLock);
        }

        private static int normalizeRotation(int rotation) {
            int normalized = rotation % 360;
            return normalized < 0 ? normalized + 360 : normalized;
        }

        public Identifier getModelId() {
            return this.modelId;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public boolean isUvLock() {
            return this.uvLock;
        }
    }
}
