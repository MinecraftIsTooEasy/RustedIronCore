package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import huix.glacier.util.Identifier;

import java.io.Reader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class JsonBlockModel {

    private static final int DEFAULT_TEXTURE_SIZE = 16;

    private final Identifier modelId;
    private final int textureWidth;
    private final int textureHeight;
    private final Map<String, String> textures;
    private final List<Element> elements;
    private final DisplayTransforms displayTransforms;
    private final boolean generatedItem;

    private JsonBlockModel(Identifier modelId, int textureWidth, int textureHeight, Map<String, String> textures, List<Element> elements, DisplayTransforms displayTransforms, boolean generatedItem) {
        this.modelId = modelId;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textures = textures;
        this.elements = elements;
        this.displayTransforms = displayTransforms;
        this.generatedItem = generatedItem;
    }

    public static JsonBlockModel fromJson(Identifier modelId, Reader reader, JsonBlockModelManager manager) {
        JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
        return fromJson(modelId, root, manager);
    }

    private static JsonBlockModel fromJson(Identifier modelId, JsonObject root, JsonBlockModelManager manager) {
        int textureWidth = DEFAULT_TEXTURE_SIZE;
        int textureHeight = DEFAULT_TEXTURE_SIZE;
        Map<String, String> textures = new HashMap<>();
        List<Element> elements = new ArrayList<>();
        DisplayTransforms displayTransforms = DisplayTransforms.EMPTY;
        boolean generatedItem = false;

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
            } else {
                BuiltinParent builtinParent = applyBuiltinParent(parentId, elements, displayTransforms);
                displayTransforms = builtinParent.displayTransforms();
                generatedItem = generatedItem || builtinParent.generatedItem();
            }
        }

        if (root.has("texture_size") && root.get("texture_size").isJsonArray()) {
            int[] textureSize = parseTextureSize(root.getAsJsonArray("texture_size"), textureWidth, textureHeight);
            textureWidth = textureSize[0];
            textureHeight = textureSize[1];
        }

        readTextures(root, textures);

        if (!textures.containsKey("all")) {
            for (Element element : elements) {
                for (Direction direction : Direction.values()) {
                    Face face = element.getFace(direction);
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
                    elements.add(Element.fromJson(jsonElement.getAsJsonObject()));
                }
            }
        }

        if (root.has("display") && root.get("display").isJsonObject()) {
            displayTransforms = DisplayTransforms.fromJson(root.getAsJsonObject("display"), displayTransforms);
        }

        if (elements.isEmpty() && !generatedItem) {
            BuiltinParent builtinParent = applyBuiltinParent(modelId, elements, displayTransforms);
            displayTransforms = builtinParent.displayTransforms();
            generatedItem = builtinParent.generatedItem();
        }

        return new JsonBlockModel(modelId, textureWidth, textureHeight, textures, elements, displayTransforms, generatedItem);
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

    private static BuiltinParent applyBuiltinParent(Identifier parentId, List<Element> elements, DisplayTransforms displayTransforms) {
        String path = parentId.getPath();
        if ("block/block".equals(path) || "block".equals(path)) {
            return new BuiltinParent(DisplayTransforms.blockDefaults(displayTransforms), false);
        }

        if ("builtin".equals(parentId.getNamespace()) && "generated".equals(path)) {
            return new BuiltinParent(DisplayTransforms.generatedItemDefaults(displayTransforms), true);
        }

        if ("item/generated".equals(path) || "generated".equals(path)) {
            return new BuiltinParent(DisplayTransforms.generatedItemDefaults(displayTransforms), true);
        }

        if ("item/handheld".equals(path) || "handheld".equals(path)) {
            return new BuiltinParent(DisplayTransforms.handheldItemDefaults(displayTransforms), true);
        }

        if ("item/handheld_rod".equals(path) || "handheld_rod".equals(path)) {
            return new BuiltinParent(DisplayTransforms.handheldRodItemDefaults(displayTransforms), true);
        }

        if ("block/cube_all".equals(path) || "cube_all".equals(path)) {
            elements.add(Element.cubeAll());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube".equals(path) || "cube".equals(path)) {
            elements.add(Element.cube());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_column".equals(path) || "cube_column".equals(path)) {
            elements.add(Element.cubeColumn());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_column_horizontal".equals(path) || "cube_column_horizontal".equals(path)) {
            elements.add(Element.cubeColumnHorizontal());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cube_bottom_top".equals(path) || "cube_bottom_top".equals(path)) {
            elements.add(Element.cubeBottomTop());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/orientable".equals(path) || "orientable".equals(path)
                || "block/orientable_with_bottom".equals(path) || "orientable_with_bottom".equals(path)) {
            elements.add(Element.orientable());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/cross".equals(path) || "cross".equals(path)) {
            elements.addAll(Element.cross());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/tinted_cross".equals(path) || "tinted_cross".equals(path)) {
            elements.addAll(Element.tintedCross());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/crop".equals(path) || "crop".equals(path)) {
            elements.addAll(Element.crop());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/leaves".equals(path) || "leaves".equals(path)) {
            elements.add(Element.cubeAll());
            return builtinBlockParent(displayTransforms);
        }

        if ("block/slab".equals(path) || "slab".equals(path)) {
            elements.add(Element.slab(false));
            return builtinBlockParent(displayTransforms);
        }

        if ("block/slab_top".equals(path) || "slab_top".equals(path)) {
            elements.add(Element.slab(true));
            return builtinBlockParent(displayTransforms);
        }

        if ("entity".equals(path)) {
            return new BuiltinParent(displayTransforms, false);
        }

        return new BuiltinParent(displayTransforms, false);
    }

    private static BuiltinParent builtinBlockParent(DisplayTransforms displayTransforms) {
        // Vanilla block templates inherit block/block. Preserve explicit child
        // transforms while supplying the inherited GUI/hand/ground defaults.
        return new BuiltinParent(DisplayTransforms.blockDefaults(displayTransforms), false);
    }

    private record BuiltinParent(DisplayTransforms displayTransforms, boolean generatedItem) {
    }

    public List<Element> getElements() {
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
        for (Element element : this.elements) {
            for (Direction direction : Direction.values()) {
                Face face = element.getFace(direction);
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

    public DisplayTransform getDisplayTransform(RenderContext context) {
        return this.displayTransforms.get(context);
    }

    public boolean isGeneratedItem() {
        return this.generatedItem;
    }

    public String getGeneratedItemLayerTexture(int layer) {
        if (layer < 0 || layer > 4) {
            return null;
        }
        return this.resolveTexture("#layer" + layer);
    }

    public static final class Element {
        private final String name;
        private final float fromX;
        private final float fromY;
        private final float fromZ;
        private final float toX;
        private final float toY;
        private final float toZ;
        private final Rotation rotation;
        private final EnumMap<Direction, Face> faces;
        private final boolean shade;

        private Element(String name, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, Rotation rotation, EnumMap<Direction, Face> faces, boolean shade) {
            this.name = name;
            this.fromX = fromX;
            this.fromY = fromY;
            this.fromZ = fromZ;
            this.toX = toX;
            this.toY = toY;
            this.toZ = toZ;
            this.rotation = rotation;
            this.faces = faces;
            this.shade = shade;
        }

        public static Element fromJson(JsonObject object) {
            float[] from = parseVector(object.getAsJsonArray("from"), 0.0F, 0.0F, 0.0F);
            float[] to = parseVector(object.getAsJsonArray("to"), 16.0F, 16.0F, 16.0F);
            float fromX = from[0];
            float fromY = from[1];
            float fromZ = from[2];
            float toX = to[0];
            float toY = to[1];
            float toZ = to[2];
            Rotation rotation = object.has("rotation") && object.get("rotation").isJsonObject()
                    ? Rotation.fromJson(object.getAsJsonObject("rotation"))
                    : Rotation.NONE;
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);

            if (object.has("faces") && object.get("faces").isJsonObject()) {
                JsonObject faceObject = object.getAsJsonObject("faces");
                for (Map.Entry<String, JsonElement> entry : faceObject.entrySet()) {
                    Direction direction = Direction.fromName(entry.getKey());
                    if (direction != null && entry.getValue().isJsonObject()) {
                        faces.put(direction, Face.fromJson(entry.getValue().getAsJsonObject()));
                    }
                }
            }

            String name = object.has("name") ? object.get("name").getAsString() : "";
            boolean shade = !object.has("shade") || object.get("shade").getAsBoolean();
            return new Element(name, fromX, fromY, fromZ, toX, toY, toZ, rotation, faces, shade);
        }

        private static Element cubeAll() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values()) {
                faces.put(direction, Face.cull("#all", direction));
            }
            return new Element("cube_all", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static Element cube() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.cull("#down", Direction.DOWN));
            faces.put(Direction.UP, Face.cull("#up", Direction.UP));
            faces.put(Direction.NORTH, Face.cull("#north", Direction.NORTH));
            faces.put(Direction.SOUTH, Face.cull("#south", Direction.SOUTH));
            faces.put(Direction.WEST, Face.cull("#west", Direction.WEST));
            faces.put(Direction.EAST, Face.cull("#east", Direction.EAST));
            return new Element("cube", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static Element cubeColumn() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.cull("#end", Direction.DOWN));
            faces.put(Direction.UP, Face.cull("#end", Direction.UP));
            faces.put(Direction.NORTH, Face.cull("#side", Direction.NORTH));
            faces.put(Direction.SOUTH, Face.cull("#side", Direction.SOUTH));
            faces.put(Direction.WEST, Face.cull("#side", Direction.WEST));
            faces.put(Direction.EAST, Face.cull("#side", Direction.EAST));
            return new Element("cube_column", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static Element cubeColumnHorizontal() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.cull("#end", Direction.DOWN));
            faces.put(Direction.UP, Face.cull("#end", Direction.UP, 180));
            faces.put(Direction.NORTH, Face.cull("#side", Direction.NORTH));
            faces.put(Direction.SOUTH, Face.cull("#side", Direction.SOUTH));
            faces.put(Direction.WEST, Face.cull("#side", Direction.WEST));
            faces.put(Direction.EAST, Face.cull("#side", Direction.EAST));
            return new Element("cube_column_horizontal", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static Element cubeBottomTop() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.cull("#bottom", Direction.DOWN));
            faces.put(Direction.UP, Face.cull("#top", Direction.UP));
            faces.put(Direction.NORTH, Face.cull("#side", Direction.NORTH));
            faces.put(Direction.SOUTH, Face.cull("#side", Direction.SOUTH));
            faces.put(Direction.WEST, Face.cull("#side", Direction.WEST));
            faces.put(Direction.EAST, Face.cull("#side", Direction.EAST));
            return new Element("cube_bottom_top", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static Element orientable() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.simple("#bottom"));
            faces.put(Direction.UP, Face.simple("#top"));
            faces.put(Direction.NORTH, Face.simple("#front"));
            faces.put(Direction.SOUTH, Face.simple("#side"));
            faces.put(Direction.WEST, Face.simple("#side"));
            faces.put(Direction.EAST, Face.simple("#side"));
            return new Element("orientable", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces, true);
        }

        private static List<Element> cross() {
            List<Element> elements = new ArrayList<>(2);
            EnumMap<Direction, Face> firstFaces = new EnumMap<>(Direction.class);
            firstFaces.put(Direction.NORTH, Face.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
            firstFaces.put(Direction.SOUTH, Face.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
            elements.add(new Element("cross_0", 0.8F, 0.0F, 8.0F, 15.2F, 16.0F, 8.0F,
                    Rotation.of("y", 45.0F, 8.0F, 8.0F, 8.0F, true), firstFaces, false));

            EnumMap<Direction, Face> secondFaces = new EnumMap<>(Direction.class);
            secondFaces.put(Direction.WEST, Face.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
            secondFaces.put(Direction.EAST, Face.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
            elements.add(new Element("cross_1", 8.0F, 0.0F, 0.8F, 8.0F, 16.0F, 15.2F,
                    Rotation.of("y", 45.0F, 8.0F, 8.0F, 8.0F, true), secondFaces, false));
            return elements;
        }

        private static List<Element> tintedCross() {
            List<Element> elements = cross();
            List<Element> tinted = new ArrayList<>(elements.size());
            for (Element element : elements) {
                EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
                for (Map.Entry<Direction, Face> entry : element.faces.entrySet()) {
                    faces.put(entry.getKey(), Face.tinted(entry.getValue().getTexture(), 0));
                }
                tinted.add(new Element(element.name, element.fromX, element.fromY, element.fromZ, element.toX, element.toY, element.toZ, element.rotation, faces, element.shade));
            }
            return tinted;
        }

        private static List<Element> crop() {
            List<Element> elements = new ArrayList<>(4);
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.WEST, Face.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
            faces.put(Direction.EAST, Face.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
            elements.add(new Element("crop_west", 4.0F, -1.0F, 0.0F, 4.0F, 15.0F, 16.0F, Rotation.NONE, faces, false));

            faces = new EnumMap<>(Direction.class);
            faces.put(Direction.WEST, Face.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
            faces.put(Direction.EAST, Face.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
            elements.add(new Element("crop_east", 12.0F, -1.0F, 0.0F, 12.0F, 15.0F, 16.0F, Rotation.NONE, faces, false));

            faces = new EnumMap<>(Direction.class);
            faces.put(Direction.NORTH, Face.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
            faces.put(Direction.SOUTH, Face.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
            elements.add(new Element("crop_north", 0.0F, -1.0F, 4.0F, 16.0F, 15.0F, 4.0F, Rotation.NONE, faces, false));

            faces = new EnumMap<>(Direction.class);
            faces.put(Direction.NORTH, Face.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
            faces.put(Direction.SOUTH, Face.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
            elements.add(new Element("crop_south", 0.0F, -1.0F, 12.0F, 16.0F, 15.0F, 12.0F, Rotation.NONE, faces, false));
            return elements;
        }

        private static Element slab(boolean top) {
            float fromY = top ? 8.0F : 0.0F;
            float toY = top ? 16.0F : 8.0F;
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.cull("#bottom", 0.0F, 0.0F, 16.0F, 16.0F, top ? null : Direction.DOWN));
            faces.put(Direction.UP, Face.cull("#top", 0.0F, 0.0F, 16.0F, 16.0F, top ? Direction.UP : null));
            faces.put(Direction.NORTH, Face.cull("#side", 0.0F, top ? 0.0F : 8.0F, 16.0F, top ? 8.0F : 16.0F, Direction.NORTH));
            faces.put(Direction.SOUTH, Face.cull("#side", 0.0F, top ? 0.0F : 8.0F, 16.0F, top ? 8.0F : 16.0F, Direction.SOUTH));
            faces.put(Direction.WEST, Face.cull("#side", 0.0F, top ? 0.0F : 8.0F, 16.0F, top ? 8.0F : 16.0F, Direction.WEST));
            faces.put(Direction.EAST, Face.cull("#side", 0.0F, top ? 0.0F : 8.0F, 16.0F, top ? 8.0F : 16.0F, Direction.EAST));
            return new Element(top ? "slab_top" : "slab", 0.0F, fromY, 0.0F, 16.0F, toY, 16.0F, Rotation.NONE, faces, true);
        }

        public String getName() {
            return this.name;
        }

        public float getFromX() {
            return this.fromX;
        }

        public float getFromY() {
            return this.fromY;
        }

        public float getFromZ() {
            return this.fromZ;
        }

        public float getToX() {
            return this.toX;
        }

        public float getToY() {
            return this.toY;
        }

        public float getToZ() {
            return this.toZ;
        }

        public Rotation getRotation() {
            return this.rotation;
        }

        public Face getFace(Direction direction) {
            return this.faces.get(direction);
        }

        public boolean isShade() {
            return this.shade;
        }

        private static float[] parseVector(JsonArray array, float x, float y, float z) {
            if (array == null || array.size() < 3) {
                return new float[]{x, y, z};
            }

            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
        }
    }

    public static final class Face {
        private final float[] uv;
        private final String texture;
        private final int rotation;
        private final Direction cullFace;
        private final int tintIndex;

        private Face(float[] uv, String texture, int rotation, Direction cullFace, int tintIndex) {
            this.uv = uv;
            this.texture = texture;
            this.rotation = rotation;
            this.cullFace = cullFace;
            this.tintIndex = tintIndex;
        }

        public static Face fromJson(JsonObject object) {
            float[] uv = object.has("uv") && object.get("uv").isJsonArray()
                    ? parseFaceUv(object.getAsJsonArray("uv"))
                    : null;
            String texture = object.has("texture") ? object.get("texture").getAsString() : null;
            int rotation = object.has("rotation") ? object.get("rotation").getAsInt() : 0;
            Direction cullFace = object.has("cullface") ? Direction.fromName(object.get("cullface").getAsString()) : null;
            int tintIndex = object.has("tintindex") ? object.get("tintindex").getAsInt() : -1;
            return new Face(uv, texture, rotation, cullFace, tintIndex);
        }

        private static Face simple(String texture) {
            return new Face(null, texture, 0, null, -1);
        }

        private static Face tinted(String texture, int tintIndex) {
            return new Face(null, texture, 0, null, tintIndex);
        }

        private static Face cull(String texture, Direction cullFace) {
            return new Face(null, texture, 0, cullFace, -1);
        }

        private static Face cull(String texture, Direction cullFace, int rotation) {
            return new Face(null, texture, rotation, cullFace, -1);
        }

        private static Face uv(String texture, float minU, float minV, float maxU, float maxV) {
            return new Face(new float[]{minU, minV, maxU, maxV}, texture, 0, null, -1);
        }

        private static Face cull(String texture, float minU, float minV, float maxU, float maxV, Direction cullFace) {
            return new Face(new float[]{minU, minV, maxU, maxV}, texture, 0, cullFace, -1);
        }

        public float[] getUv(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, Direction direction, int textureWidth, int textureHeight) {
            if (this.uv != null) {
                return this.uv;
            }

            return switch (direction) {
                case DOWN -> new float[]{fromX, 16.0F - toZ, toX, 16.0F - fromZ};
                case UP -> new float[]{fromX, fromZ, toX, toZ};
                case NORTH -> new float[]{16.0F - toX, 16.0F - toY, 16.0F - fromX, 16.0F - fromY};
                case SOUTH -> new float[]{fromX, 16.0F - toY, toX, 16.0F - fromY};
                case WEST -> new float[]{fromZ, 16.0F - toY, toZ, 16.0F - fromY};
                case EAST -> new float[]{16.0F - toZ, 16.0F - toY, 16.0F - fromZ, 16.0F - fromY};
            };
        }

        public String getTexture() {
            return this.texture;
        }

        public int getRotation() {
            return this.rotation;
        }

        public Direction getCullFace() {
            return this.cullFace;
        }

        public int getTintIndex() {
            return this.tintIndex;
        }

        private static float[] parseFaceUv(JsonArray array) {
            if (array.size() < 4) {
                return null;
            }

            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat()};
        }
    }

    public static final class Rotation {
        public static final Rotation NONE = new Rotation("", 0.0F, new float[]{8.0F, 8.0F, 8.0F}, false);

        private final String axis;
        private final float angle;
        private final float[] origin;
        private final boolean rescale;

        private Rotation(String axis, float angle, float[] origin, boolean rescale) {
            this.axis = axis;
            this.angle = angle;
            this.origin = origin;
            this.rescale = rescale;
        }

        public static Rotation fromJson(JsonObject object) {
            String axis = object.has("axis") ? object.get("axis").getAsString().toLowerCase(Locale.ROOT) : "";
            float angle = object.has("angle") ? object.get("angle").getAsFloat() : 0.0F;
            float[] origin = object.has("origin") && object.get("origin").isJsonArray()
                    ? Element.parseVector(object.getAsJsonArray("origin"), 8.0F, 8.0F, 8.0F)
                    : new float[]{8.0F, 8.0F, 8.0F};
            boolean rescale = object.has("rescale") && object.get("rescale").getAsBoolean();
            return new Rotation(axis, angle, origin, rescale);
        }

        public static Rotation of(String axis, float angle, float originX, float originY, float originZ) {
            return new Rotation(axis, angle, new float[]{originX, originY, originZ}, false);
        }

        public static Rotation of(String axis, float angle, float originX, float originY, float originZ,
                                  boolean rescale) {
            return new Rotation(axis, angle, new float[]{originX, originY, originZ}, rescale);
        }

        public boolean isPresent() {
            return this.angle != 0.0F && !this.axis.isEmpty();
        }

        public String getAxis() {
            return this.axis;
        }

        public float getAngle() {
            return this.angle;
        }

        public float[] getOrigin() {
            return this.origin;
        }

        public boolean isRescale() {
            return this.rescale;
        }
    }

    public static final class DisplayTransforms {
        private static final DisplayTransforms EMPTY = new DisplayTransforms(new HashMap<>());

        private final Map<RenderContext, DisplayTransform> transforms;

        private DisplayTransforms(Map<RenderContext, DisplayTransform> transforms) {
            this.transforms = transforms;
        }

        public static DisplayTransforms fromJson(JsonObject object, DisplayTransforms parent) {
            Map<RenderContext, DisplayTransform> transforms = new HashMap<>(parent.transforms);
            for (RenderContext context : RenderContext.values()) {
                String key = context.getJsonName();
                if (key != null && object.has(key) && object.get(key).isJsonObject()) {
                    transforms.put(context, DisplayTransform.fromJson(object.getAsJsonObject(key)));
                }
            }
            return new DisplayTransforms(transforms);
        }

        private static DisplayTransforms blockDefaults(DisplayTransforms parent) {
            Map<RenderContext, DisplayTransform> transforms = new HashMap<>(parent.transforms);
            transforms.putIfAbsent(RenderContext.GUI, DisplayTransform.of(30.0F, 225.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F, 0.625F, 0.625F));
            transforms.putIfAbsent(RenderContext.GROUND, DisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.25F, 0.25F, 0.25F));
            transforms.putIfAbsent(RenderContext.FIXED, DisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F));
            transforms.putIfAbsent(RenderContext.THIRD_PERSON_RIGHT_HAND, DisplayTransform.of(75.0F, 45.0F, 0.0F, 0.0F, 2.5F, 0.0F, 0.375F, 0.375F, 0.375F));
            transforms.putIfAbsent(RenderContext.FIRST_PERSON_RIGHT_HAND, DisplayTransform.of(0.0F, 45.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F, 0.4F, 0.4F));
            transforms.putIfAbsent(RenderContext.FIRST_PERSON_LEFT_HAND, DisplayTransform.of(0.0F, 225.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F, 0.4F, 0.4F));
            return new DisplayTransforms(transforms);
        }

        private static DisplayTransforms generatedItemDefaults(DisplayTransforms parent) {
            Map<RenderContext, DisplayTransform> transforms = new HashMap<>(parent.transforms);
            transforms.putIfAbsent(RenderContext.GUI, DisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
            transforms.putIfAbsent(RenderContext.GROUND, DisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.5F, 0.5F, 0.5F));
            transforms.putIfAbsent(RenderContext.HEAD, DisplayTransform.of(0.0F, 180.0F, 0.0F, 0.0F, 13.0F, 7.0F, 1.0F, 1.0F, 1.0F));
            transforms.putIfAbsent(RenderContext.THIRD_PERSON_RIGHT_HAND, DisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.55F, 0.55F, 0.55F));
            transforms.putIfAbsent(RenderContext.FIRST_PERSON_RIGHT_HAND, DisplayTransform.of(0.0F, -90.0F, 25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
            transforms.putIfAbsent(RenderContext.FIXED, DisplayTransform.of(0.0F, 180.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
            return new DisplayTransforms(transforms);
        }

        private static DisplayTransforms handheldItemDefaults(DisplayTransforms parent) {
            Map<RenderContext, DisplayTransform> transforms = new HashMap<>(generatedItemDefaults(parent).transforms);
            transforms.put(RenderContext.THIRD_PERSON_RIGHT_HAND, DisplayTransform.of(0.0F, -90.0F, 55.0F, 0.0F, 4.0F, 0.5F, 0.85F, 0.85F, 0.85F));
            transforms.put(RenderContext.THIRD_PERSON_LEFT_HAND, DisplayTransform.of(0.0F, 90.0F, -55.0F, 0.0F, 4.0F, 0.5F, 0.85F, 0.85F, 0.85F));
            transforms.put(RenderContext.FIRST_PERSON_RIGHT_HAND, DisplayTransform.of(0.0F, -90.0F, 25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
            transforms.put(RenderContext.FIRST_PERSON_LEFT_HAND, DisplayTransform.of(0.0F, 90.0F, -25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
            return new DisplayTransforms(transforms);
        }

        private static DisplayTransforms handheldRodItemDefaults(DisplayTransforms parent) {
            return handheldItemDefaults(parent);
        }

        public DisplayTransform get(RenderContext context) {
            DisplayTransform transform = this.transforms.get(context);
            if (transform != null) {
                return transform;
            }
            if (context == RenderContext.FIRST_PERSON_LEFT_HAND) {
                return this.transforms.get(RenderContext.FIRST_PERSON_RIGHT_HAND);
            }
            if (context == RenderContext.THIRD_PERSON_LEFT_HAND) {
                return this.transforms.get(RenderContext.THIRD_PERSON_RIGHT_HAND);
            }
            return this.transforms.get(RenderContext.FIXED);
        }
    }

    public static final class DisplayTransform {
        private final float[] rotation;
        private final float[] translation;
        private final float[] scale;
        private final float[] rightRotation;

        private DisplayTransform(float[] rotation, float[] translation, float[] scale, float[] rightRotation) {
            this.rotation = rotation;
            this.translation = translation;
            this.scale = scale;
            this.rightRotation = rightRotation;
        }

        public static DisplayTransform fromJson(JsonObject object) {
            float[] rotation = object.has("rotation") && object.get("rotation").isJsonArray()
                    ? parseTransformVector(object.getAsJsonArray("rotation"), 0.0F, 0.0F, 0.0F)
                    : new float[]{0.0F, 0.0F, 0.0F};
            float[] translation = object.has("translation") && object.get("translation").isJsonArray()
                    ? parseTransformVector(object.getAsJsonArray("translation"), 0.0F, 0.0F, 0.0F)
                    : new float[]{0.0F, 0.0F, 0.0F};
            float[] scale = object.has("scale") && object.get("scale").isJsonArray()
                    ? parseTransformVector(object.getAsJsonArray("scale"), 1.0F, 1.0F, 1.0F)
                    : new float[]{1.0F, 1.0F, 1.0F};
            float[] rightRotation = object.has("right_rotation") && object.get("right_rotation").isJsonArray()
                    ? parseTransformVector(object.getAsJsonArray("right_rotation"), 0.0F, 0.0F, 0.0F)
                    : new float[]{0.0F, 0.0F, 0.0F};
            return new DisplayTransform(rotation, translation, scale, rightRotation);
        }

        private static DisplayTransform of(float rotationX, float rotationY, float rotationZ, float translationX, float translationY, float translationZ, float scaleX, float scaleY, float scaleZ) {
            return new DisplayTransform(
                    new float[]{rotationX, rotationY, rotationZ},
                    new float[]{translationX, translationY, translationZ},
                    new float[]{scaleX, scaleY, scaleZ},
                    new float[]{0.0F, 0.0F, 0.0F}
            );
        }

        public float[] getRotation() {
            return this.rotation;
        }

        public float[] getTranslation() {
            return this.translation;
        }

        public float[] getScale() {
            return this.scale;
        }

        public float[] getRightRotation() {
            return this.rightRotation;
        }

        private static float[] parseTransformVector(JsonArray array, float x, float y, float z) {
            if (array == null || array.size() < 3) {
                return new float[]{x, y, z};
            }
            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
        }
    }

    public enum RenderContext {
        BLOCK("fixed"),
        BLOCK_ITEM("fixed"),
        NONE("none"),
        GUI("gui"),
        GROUND("ground"),
        FIXED("fixed"),
        HEAD("head"),
        FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
        FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
        THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
        THIRD_PERSON_LEFT_HAND("thirdperson_lefthand");

        private final String jsonName;

        RenderContext(String jsonName) {
            this.jsonName = jsonName;
        }

        public String getJsonName() {
            return this.jsonName;
        }
    }

    public enum Direction {
        DOWN(0),
        UP(1),
        NORTH(2),
        SOUTH(3),
        WEST(4),
        EAST(5);

        private final int side;

        Direction(int side) {
            this.side = side;
        }

        public int getSide() {
            return this.side;
        }

        public static Direction fromName(String name) {
            return switch (name) {
                case "down" -> DOWN;
                case "up" -> UP;
                case "north" -> NORTH;
                case "south" -> SOUTH;
                case "west" -> WEST;
                case "east" -> EAST;
                default -> null;
            };
        }
    }
}
