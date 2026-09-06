package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import huix.glacier.util.Identifier;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class JsonBlockModel {

    private static final int DEFAULT_TEXTURE_SIZE = 16;

    private final Identifier modelId;
    private final int textureWidth;
    private final int textureHeight;
    private final Map<String, String> textures;
    private final List<JsonModelElement> elements;
    private final List<JsonItemModelOverride> overrides;
    private final JsonDisplayTransforms displayTransforms;
    private final boolean generatedItem;
    private final boolean builtInEntity;

    private JsonBlockModel(Identifier modelId, int textureWidth, int textureHeight, Map<String, String> textures, List<JsonModelElement> elements, List<JsonItemModelOverride> overrides, JsonDisplayTransforms displayTransforms, boolean generatedItem, boolean builtInEntity) {
        this.modelId = modelId;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textures = textures;
        this.elements = elements;
        this.overrides = overrides;
        this.displayTransforms = displayTransforms;
        this.generatedItem = generatedItem;
        this.builtInEntity = builtInEntity;
    }

    public static JsonBlockModel fromJson(Identifier modelId, Reader reader, JsonBlockModelManager manager) {
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        return fromJson(modelId, root, manager);
    }

    private static JsonBlockModel fromJson(Identifier modelId, JsonObject root, JsonBlockModelManager manager) {
        int textureWidth = DEFAULT_TEXTURE_SIZE;
        int textureHeight = DEFAULT_TEXTURE_SIZE;
        Map<String, String> textures = new HashMap<>();
        List<JsonModelElement> elements = new ArrayList<>();
        List<JsonItemModelOverride> overrides = new ArrayList<>();
        JsonDisplayTransforms displayTransforms = JsonDisplayTransforms.EMPTY;
        boolean generatedItem = false;
        boolean builtInEntity = false;

        if (root.has("parent")) {
            Identifier parentId = normalizeParentId(root.get("parent").getAsString());
            Optional<JsonBlockModel> parent = manager.loadModelById(parentId);
            if (parent.isPresent()) {
                JsonBlockModel parentModel = parent.get();
                textureWidth = parentModel.textureWidth;
                textureHeight = parentModel.textureHeight;
                textures.putAll(parentModel.textures);
                elements.addAll(parentModel.elements);
                displayTransforms = parentModel.displayTransforms;
                generatedItem = parentModel.generatedItem;
                builtInEntity = parentModel.builtInEntity;
            } else {
                JsonBuiltinParent builtinParent = applyBuiltinParent(parentId, elements, displayTransforms);
                displayTransforms = builtinParent.displayTransforms();
                generatedItem = generatedItem || builtinParent.generatedItem();
                builtInEntity = builtinParent.builtInEntity();
            }
        }

        if (root.has("texture_size") && root.get("texture_size").isJsonArray()) {
            int[] textureSize = parseTextureSize(root.getAsJsonArray("texture_size"), textureWidth, textureHeight);
            textureWidth = textureSize[0];
            textureHeight = textureSize[1];
        }

        readTextures(root, textures);

        if (!textures.containsKey("all")) {
            for (JsonModelElement element : elements) {
                for (JsonModelDirection direction : JsonModelDirection.values()) {
                    JsonModelFace face = element.getFace(direction);
                    if (face != null && "#all".equals(face.getTexture())) {
                        String particle = textures.get("particle");
                        if (particle != null) {
                            textures.put("all", particle);
                        }
                        break;
                    }
                }
            }
        }

        if (root.has("elements") && root.get("elements").isJsonArray()) {
            elements.clear();
            for (JsonElement jsonElement : root.getAsJsonArray("elements")) {
                if (jsonElement.isJsonObject()) {
                    elements.add(JsonModelElement.fromJson(jsonElement.getAsJsonObject()));
                }
            }
        }

        if (root.has("display") && root.get("display").isJsonObject()) {
            displayTransforms = JsonDisplayTransforms.fromJson(root.getAsJsonObject("display"), displayTransforms);
        }

        if (root.has("overrides") && root.get("overrides").isJsonArray()) {
            for (JsonElement overrideElement : root.getAsJsonArray("overrides")) {
                if (overrideElement.isJsonObject()) {
                    JsonItemModelOverride override = JsonItemModelOverride.fromJson(overrideElement.getAsJsonObject());
                    if (override != null) {
                        overrides.add(override);
                    }
                }
            }
        }

        if (elements.isEmpty() && !generatedItem) {
            JsonBuiltinParent builtinParent = applyBuiltinParent(modelId, elements, displayTransforms);
            displayTransforms = builtinParent.displayTransforms();
            generatedItem = builtinParent.generatedItem();
            builtInEntity = builtInEntity || builtinParent.builtInEntity();
        }

        return new JsonBlockModel(modelId, textureWidth, textureHeight, textures, elements, overrides,
                displayTransforms, generatedItem, builtInEntity && elements.isEmpty());
    }

    private static Identifier normalizeParentId(String rawParent) {
        if (rawParent.startsWith("builtin/")) {
            return new Identifier("builtin", rawParent.substring("builtin/".length()));
        }
        Identifier parentId = new Identifier(rawParent);
        String path = parentId.getPath();
        if (path.indexOf('/') < 0) {
            return new Identifier(parentId.getNamespace(), "block/" + path);
        }
        return parentId;
    }

    private static void readTextures(JsonObject root, Map<String, String> textures) {
        if (root.has("textures") && root.get("textures").isJsonObject()) {
            JsonObject textureObject = root.getAsJsonObject("textures");
            for (Map.Entry<String, JsonElement> entry : textureObject.entrySet()) {
                textures.put(entry.getKey(), entry.getValue().getAsString());
            }
        }

        if (root.has("texturesCustom") && root.get("texturesCustom").isJsonArray()) {
            for (JsonElement textureElement : root.getAsJsonArray("texturesCustom")) {
                if (!textureElement.isJsonObject()) {
                    continue;
                }
                JsonObject textureObject = textureElement.getAsJsonObject();
                if (textureObject.has("name") && textureObject.has("src")) {
                    textures.put(textureObject.get("name").getAsString(), textureObject.get("src").getAsString());
                }
            }
        }
    }

    private static int[] parseTextureSize(JsonArray array, int fallbackWidth, int fallbackHeight) {
        if (array.size() < 2) {
            return new int[]{fallbackWidth, fallbackHeight};
        }
        int width = Math.max(1, array.get(0).getAsInt());
        int height = Math.max(1, array.get(1).getAsInt());
        return new int[]{width, height};
    }

    private static JsonBuiltinParent applyBuiltinParent(Identifier parentId, List<JsonModelElement> elements, JsonDisplayTransforms displayTransforms) {
        String path = parentId.getPath();
        if ("block/block".equals(path) || "block".equals(path)) {
            return new JsonBuiltinParent(JsonDisplayTransforms.blockDefaults(displayTransforms), false, false);
        }

        if ("builtin".equals(parentId.getNamespace()) && "generated".equals(path)) {
            return new JsonBuiltinParent(JsonDisplayTransforms.generatedItemDefaults(displayTransforms), true, false);
        }

        if ("item/generated".equals(path) || "generated".equals(path)) {
            return new JsonBuiltinParent(JsonDisplayTransforms.generatedItemDefaults(displayTransforms), true, false);
        }

        if ("item/handheld".equals(path) || "handheld".equals(path)) {
            return new JsonBuiltinParent(JsonDisplayTransforms.handheldItemDefaults(displayTransforms), true, false);
        }

        if ("item/handheld_rod".equals(path) || "handheld_rod".equals(path)) {
            return new JsonBuiltinParent(JsonDisplayTransforms.handheldRodItemDefaults(displayTransforms), true, false);
        }

        if ("block/cube_all".equals(path) || "cube_all".equals(path)) {
            elements.add(JsonModelElement.cubeAll());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube".equals(path) || "cube".equals(path)) {
            elements.add(JsonModelElement.cube());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_column".equals(path) || "cube_column".equals(path)) {
            elements.add(JsonModelElement.cubeColumn());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_column_horizontal".equals(path) || "cube_column_horizontal".equals(path)) {
            elements.add(JsonModelElement.cubeColumnHorizontal());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_bottom_top".equals(path) || "cube_bottom_top".equals(path)) {
            elements.add(JsonModelElement.cubeBottomTop());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/orientable".equals(path) || "orientable".equals(path)
                || "block/orientable_with_bottom".equals(path) || "orientable_with_bottom".equals(path)) {
            elements.add(JsonModelElement.orientable());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cross".equals(path) || "cross".equals(path)) {
            elements.addAll(JsonModelElement.cross());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/tinted_cross".equals(path) || "tinted_cross".equals(path)) {
            elements.addAll(JsonModelElement.tintedCross());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/crop".equals(path) || "crop".equals(path)) {
            elements.addAll(JsonModelElement.crop());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/leaves".equals(path) || "leaves".equals(path)) {
            elements.add(JsonModelElement.cubeAll());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/slab".equals(path) || "slab".equals(path)) {
            elements.add(JsonModelElement.slab(false));
            return builtinBlockParent(displayTransforms);
        }

        if ("block/slab_top".equals(path) || "slab_top".equals(path)) {
            elements.add(JsonModelElement.slab(true));
            return builtinBlockParent(displayTransforms);
        }

        if ("builtin".equals(parentId.getNamespace()) && "entity".equals(path)) {
            return new JsonBuiltinParent(displayTransforms, false, true);
        }

        return new JsonBuiltinParent(displayTransforms, false, false);
    }

    private static JsonBuiltinParent builtinBlockParent(JsonDisplayTransforms displayTransforms) {
        // Vanilla block templates inherit block/block. Preserve explicit child
        // transforms while supplying the inherited GUI/hand/ground defaults.
        return new JsonBuiltinParent(JsonDisplayTransforms.blockDefaults(displayTransforms), false, false);
    }


    public List<JsonModelElement> getElements() {
        return this.elements;
    }

    public String resolveTexture(String textureReference) {
        return this.resolveTexture(textureReference, new HashSet<>());
    }

    private String resolveTexture(String textureReference, Set<String> visited) {
        if (textureReference == null) {
            return null;
        }

        if (!textureReference.startsWith("#")) {
            return textureReference;
        }

        String key = textureReference.substring(1);
        if (!visited.add(key)) {
            return null;
        }

        String mappedTexture = this.textures.get(key);
        if (mappedTexture == null) {
            return null;
        }

        return this.resolveTexture(mappedTexture, visited);
    }

    public Set<String> collectResolvedTextures() {
        Set<String> resolvedTextures = new HashSet<>();
        for (JsonModelElement element : this.elements) {
            for (JsonModelDirection direction : JsonModelDirection.values()) {
                JsonModelFace face = element.getFace(direction);
                if (face == null) {
                    continue;
                }
                String texture = this.resolveTexture(face.getTexture());
                if (texture != null) {
                    resolvedTextures.add(texture);
                }
            }
        }
        if (this.generatedItem) {
            for (int layer = 0; layer < 5; layer++) {
                String texture = this.resolveTexture("#layer" + layer);
                if (texture != null) {
                    resolvedTextures.add(texture);
                } else if (layer == 0) {
                    break;
                }
            }
        }
        return resolvedTextures;
    }

    public Identifier getModelId() {
        return this.modelId;
    }

    public int getTextureWidth() {
        return this.textureWidth;
    }

    public int getTextureHeight() {
        return this.textureHeight;
    }

    public JsonDisplayTransform getDisplayTransform(JsonModelDisplayContext context) {
        return this.displayTransforms.get(context);
    }

    public boolean isGeneratedItem() {
        return this.generatedItem;
    }

    public boolean isBuiltInEntity() {
        return this.builtInEntity;
    }

    public List<JsonItemModelOverride> getOverrides() {
        return this.overrides;
    }

    public String getGeneratedItemLayerTexture(int layer) {
        if (layer < 0 || layer > 4) {
            return null;
        }
        return this.resolveTexture("#layer" + layer);
    }
}
