package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import huix.glacier.util.Identifier;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class JsonBlockState {

    public static final Variant EMPTY_VARIANT = new Variant(null, 0, 0, false, 1);
    private static final List<Variant> EMPTY_VARIANTS = Collections.emptyList();

    private final Identifier sourceId;
    private final Map<String, MultiVariant> variants = new LinkedHashMap<>();
    private final List<MultipartPart> multipart = new ArrayList<>();

    private JsonBlockState(Identifier sourceId) {
        this.sourceId = sourceId;
    }

    public static JsonBlockState fromJson(Identifier sourceId, Reader reader) {
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        JsonBlockState blockState = new JsonBlockState(sourceId);

        if (root.has("variants") && root.get("variants").isJsonObject()) {
            JsonObject variants = root.getAsJsonObject("variants");
            for (Map.Entry<String, JsonElement> entry : variants.entrySet()) {
                MultiVariant variant = MultiVariant.fromJson(entry.getValue());
                if (!variant.isEmpty()) {
                    blockState.variants.put(entry.getKey(), variant);
                }
            }
        } else if (!root.has("multipart")) {
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                MultiVariant variant = MultiVariant.fromJson(entry.getValue());
                if (!variant.isEmpty()) {
                    blockState.variants.put(entry.getKey(), variant);
                }
            }
        }

        if (root.has("multipart") && root.get("multipart").isJsonArray()) {
            for (JsonElement element : root.getAsJsonArray("multipart")) {
                if (element.isJsonObject()) {
                    MultipartPart part = MultipartPart.fromJson(element.getAsJsonObject());
                    if (part != null && !part.getVariants().isEmpty()) {
                        blockState.multipart.add(part);
                    }
                }
            }
        }

        return blockState;
    }

    public Variant getVariant(int metadata) {
        List<Variant> selected = this.getVariants(metadata, defaultProperties(metadata));
        return selected.isEmpty() ? EMPTY_VARIANT : selected.get(0);
    }

    public List<Variant> getVariants(int metadata, Map<String, String> properties) {
        Map<String, String> stateProperties = properties == null ? defaultProperties(metadata) : properties;
        if (!this.multipart.isEmpty()) {
            List<Variant> selected = new ArrayList<>();
            for (MultipartPart part : this.multipart) {
                if (part.matches(stateProperties)) {
                    Variant variant = part.getSelectedVariant();
                    if (variant.getModelId() != null) {
                        selected.add(variant);
                    }
                }
            }
            return selected;
        }

        MultiVariant variant = this.findVariant(metadata, stateProperties);
        if (variant == null || variant.isEmpty()) {
            return EMPTY_VARIANTS;
        }
        Variant selected = variant.getSelectedVariant();
        return selected.getModelId() == null ? EMPTY_VARIANTS : Collections.singletonList(selected);
    }

    public Set<Identifier> collectModelIds() {
        Set<Identifier> modelIds = new HashSet<>();
        for (MultiVariant variant : this.variants.values()) {
            variant.collectModelIds(modelIds);
        }
        for (MultipartPart part : this.multipart) {
            for (Variant variant : part.getVariants()) {
                if (variant.getModelId() != null) {
                    modelIds.add(variant.getModelId());
                }
            }
        }
        return modelIds;
    }

    private MultiVariant findVariant(int metadata, Map<String, String> properties) {
        String metadataKey = Integer.toString(metadata);
        MultiVariant variant = this.variants.get(metadataKey);
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

        variant = this.findPropertyVariant(properties);
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

        return this.variants.size() == 1 ? this.variants.values().iterator().next() : null;
    }

    private MultiVariant findPropertyVariant(Map<String, String> properties) {
        MultiVariant bestVariant = null;
        int bestSpecificity = -1;
        for (Map.Entry<String, MultiVariant> entry : this.variants.entrySet()) {
            int specificity = matchPropertyExpression(entry.getKey(), properties);
            if (specificity > bestSpecificity) {
                bestSpecificity = specificity;
                bestVariant = entry.getValue();
            }
        }
        return bestSpecificity >= 0 ? bestVariant : null;
    }

    private static Map<String, String> defaultProperties(int metadata) {
        Map<String, String> properties = new LinkedHashMap<>();
        addDefaultProperties(properties, metadata);
        return properties;
    }

    static void addDefaultProperties(Map<String, String> properties, int metadata) {
        put(properties, "metadata", Integer.toString(metadata));
        put(properties, "meta", Integer.toString(metadata));
        put(properties, "damage", Integer.toString(metadata));
        put(properties, "north", booleanString((metadata & 1) != 0));
        put(properties, "east", booleanString((metadata & 2) != 0));
        put(properties, "south", booleanString((metadata & 4) != 0));
        put(properties, "west", booleanString((metadata & 8) != 0));
        put(properties, "up", "false");
        put(properties, "down", "false");
        put(properties, "open", booleanString((metadata & 1) == 0));
        put(properties, "sealed", booleanString((metadata & 1) != 0));
        put(properties, "rack", "false");
        put(properties, "waterlogged", "false");
        put(properties, "snowy", "false");
        put(properties, "lit", booleanString((metadata & 4) != 0));
        put(properties, "powered", "false");
        put(properties, "enabled", "true");

        String horizontalFacing = getHorizontalFacing(metadata);
        put(properties, "facing", horizontalFacing);
        put(properties, "horizontal_facing", horizontalFacing);
    }

    private static void put(Map<String, String> properties, String key, String value) {
        properties.putIfAbsent(key, value);
    }

    private static String booleanString(boolean value) {
        return value ? "true" : "false";
    }

    private static String getHorizontalFacing(int metadata) {
        return switch (metadata & 3) {
            case 0 -> "south";
            case 1 -> "west";
            case 2 -> "north";
            case 3 -> "east";
            default -> "south";
        };
    }

    private static int matchPropertyExpression(String expression, Map<String, String> properties) {
        if (expression == null || expression.isEmpty() || "normal".equals(expression) || "default".equals(expression)) {
            return -1;
        }
        if (expression.indexOf('=') < 0) {
            return -1;
        }

        int matched = 0;
        String[] terms = expression.split(",");
        for (String rawTerm : terms) {
            String term = rawTerm.trim();
            int equalsIndex = term.indexOf('=');
            if (equalsIndex <= 0 || equalsIndex == term.length() - 1) {
                return -1;
            }

            String key = term.substring(0, equalsIndex).trim();
            String expected = term.substring(equalsIndex + 1).trim();
            if (!matchesPropertyValue(properties, key, expected)) {
                return -1;
            }
            matched++;
        }
        return matched;
    }

    private static boolean matchesPropertyValue(Map<String, String> properties, String key, String expected) {
        boolean inverted = expected.startsWith("!");
        String values = inverted ? expected.substring(1) : expected;
        String actual = getProperty(properties, key);
        boolean matched = false;
        for (String value : values.split("\\|")) {
            if (value.equals(actual)) {
                matched = true;
                break;
            }
        }
        return inverted != matched;
    }

    private static String getProperty(Map<String, String> properties, String key) {
        String value = properties.get(key);
        if (value != null) {
            return value;
        }
        return switch (key.toLowerCase(Locale.ROOT)) {
            case "open", "up", "down", "north", "east", "south", "west", "rack", "waterlogged", "snowy", "lit", "powered" -> "false";
            case "enabled" -> "true";
            default -> "";
        };
    }

    public static final class MultiVariant {
        private final List<Variant> variants;

        private MultiVariant(List<Variant> variants) {
            this.variants = variants;
        }

        private static MultiVariant fromJson(JsonElement value) {
            List<Variant> variants = new ArrayList<>();
            if (value.isJsonObject()) {
                addVariant(variants, value.getAsJsonObject());
            } else if (value.isJsonArray()) {
                for (JsonElement element : value.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        addVariant(variants, element.getAsJsonObject());
                    }
                }
            }
            return new MultiVariant(variants);
        }

        private static void addVariant(List<Variant> variants, JsonObject object) {
            Variant variant = Variant.fromJson(object);
            if (variant.getModelId() != null) {
                variants.add(variant);
            }
        }

        private boolean isEmpty() {
            return this.variants.isEmpty();
        }

        public List<Variant> getVariants() {
            return this.variants;
        }

        private Variant getSelectedVariant() {
            return this.variants.isEmpty() ? EMPTY_VARIANT : this.variants.get(0);
        }

        private void collectModelIds(Set<Identifier> modelIds) {
            for (Variant variant : this.variants) {
                if (variant.getModelId() != null) {
                    modelIds.add(variant.getModelId());
                }
            }
        }
    }

    public static final class MultipartPart {
        private final Condition condition;
        private final MultiVariant variant;

        private MultipartPart(Condition condition, MultiVariant variant) {
            this.condition = condition;
            this.variant = variant;
        }

        private static MultipartPart fromJson(JsonObject object) {
            if (!object.has("apply")) {
                return null;
            }
            Condition condition = object.has("when") && object.get("when").isJsonObject()
                    ? Condition.fromJson(object.getAsJsonObject("when"))
                    : Condition.TRUE;
            MultiVariant variant = MultiVariant.fromJson(object.get("apply"));
            return new MultipartPart(condition, variant);
        }

        private boolean matches(Map<String, String> properties) {
            return this.condition.matches(properties);
        }

        public List<Variant> getVariants() {
            return this.variant.getVariants();
        }

        private Variant getSelectedVariant() {
            return this.variant.getSelectedVariant();
        }
    }

    public interface Condition {
        Condition TRUE = properties -> true;

        boolean matches(Map<String, String> properties);

        static Condition fromJson(JsonObject object) {
            if (object.entrySet().isEmpty()) {
                return TRUE;
            }
            if (object.entrySet().size() == 1) {
                if (object.has("OR") && object.get("OR").isJsonArray()) {
                    List<Condition> conditions = readConditionList(object.getAsJsonArray("OR"));
                    return properties -> conditions.stream().anyMatch(condition -> condition.matches(properties));
                }
                if (object.has("AND") && object.get("AND").isJsonArray()) {
                    List<Condition> conditions = readConditionList(object.getAsJsonArray("AND"));
                    return properties -> conditions.stream().allMatch(condition -> condition.matches(properties));
                }
            }

            List<Condition> conditions = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();
                conditions.add(properties -> matchesPropertyValue(properties, key, value));
            }
            return properties -> conditions.stream().allMatch(condition -> condition.matches(properties));
        }

        static List<Condition> readConditionList(JsonArray array) {
            List<Condition> conditions = new ArrayList<>();
            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    conditions.add(fromJson(element.getAsJsonObject()));
                }
            }
            return conditions;
        }
    }

    public static final class Variant {
        private final Identifier modelId;
        private final int x;
        private final int y;
        private final boolean uvLock;
        private final int weight;

        private Variant(Identifier modelId, int x, int y, boolean uvLock, int weight) {
            this.modelId = modelId;
            this.x = normalizeRotation(x);
            this.y = normalizeRotation(y);
            this.uvLock = uvLock;
            this.weight = Math.max(1, weight);
        }

        public static Variant simple(Identifier modelId) {
            return new Variant(modelId, 0, 0, false, 1);
        }

        private static Variant fromJson(JsonObject object) {
            if (!object.has("model")) {
                return EMPTY_VARIANT;
            }

            Identifier modelId = new Identifier(object.get("model").getAsString());
            int x = object.has("x") ? object.get("x").getAsInt() : 0;
            int y = object.has("y") ? object.get("y").getAsInt() : 0;
            boolean uvLock = object.has("uvlock") && object.get("uvlock").getAsBoolean();
            int weight = object.has("weight") ? object.get("weight").getAsInt() : 1;
            return new Variant(modelId, x, y, uvLock, weight);
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

        public int getWeight() {
            return this.weight;
        }
    }
}
