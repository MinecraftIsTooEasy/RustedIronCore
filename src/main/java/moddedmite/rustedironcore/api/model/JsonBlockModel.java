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

    private JsonBlockModel(Identifier modelId, int textureWidth, int textureHeight, Map<String, String> textures, List<Element> elements, DisplayTransforms displayTransforms) {
        this.modelId = modelId;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textures = textures;
        this.elements = elements;
        this.displayTransforms = displayTransforms;
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
            } else {
                applyBuiltinParent(parentId, elements);
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

        if (elements.isEmpty()) {
            applyBuiltinParent(modelId, elements);
        }

        return new JsonBlockModel(modelId, textureWidth, textureHeight, textures, elements, displayTransforms);
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

    private static void applyBuiltinParent(Identifier parentId, List<Element> elements) {
        String path = parentId.getPath();
        if ("block/cube_all".equals(path) || "cube_all".equals(path)) {
            elements.add(Element.cubeAll());
            return;
        }

        if ("block/cube".equals(path) || "cube".equals(path)) {
            elements.add(Element.cube());
            return;
        }

        if ("block/cube_column".equals(path) || "cube_column".equals(path)) {
            elements.add(Element.cubeColumn());
            return;
        }

        if ("block/cube_bottom_top".equals(path) || "cube_bottom_top".equals(path)) {
            elements.add(Element.cubeBottomTop());
            return;
        }

        if ("block/orientable".equals(path) || "orientable".equals(path)) {
            elements.add(Element.orientable());
            return;
        }

        if ("block/cross".equals(path) || "cross".equals(path)) {
            elements.addAll(Element.cross());
            return;
        }

        if ("entity".equals(path)) {
            return;
        }
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

        private Element(String name, float fromX, float fromY, float fromZ, float toX, float toY, float toZ, Rotation rotation, EnumMap<Direction, Face> faces) {
            this.name = name;
            this.fromX = fromX;
            this.fromY = fromY;
            this.fromZ = fromZ;
            this.toX = toX;
            this.toY = toY;
            this.toZ = toZ;
            this.rotation = rotation;
            this.faces = faces;
        }

        public static Element fromJson(JsonObject object) {
            float[] from = parseVector(object.getAsJsonArray("from"), 0.0F, 0.0F, 0.0F);
            float[] to = parseVector(object.getAsJsonArray("to"), 16.0F, 16.0F, 16.0F);
            float fromX = Math.min(from[0], to[0]);
            float fromY = Math.min(from[1], to[1]);
            float fromZ = Math.min(from[2], to[2]);
            float toX = Math.max(from[0], to[0]);
            float toY = Math.max(from[1], to[1]);
            float toZ = Math.max(from[2], to[2]);
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
            return new Element(name, fromX, fromY, fromZ, toX, toY, toZ, rotation, faces);
        }

        private static Element cubeAll() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            for (Direction direction : Direction.values()) {
                faces.put(direction, Face.simple("#all"));
            }
            return new Element("cube_all", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces);
        }

        private static Element cube() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.simple("#down"));
            faces.put(Direction.UP, Face.simple("#up"));
            faces.put(Direction.NORTH, Face.simple("#north"));
            faces.put(Direction.SOUTH, Face.simple("#south"));
            faces.put(Direction.WEST, Face.simple("#west"));
            faces.put(Direction.EAST, Face.simple("#east"));
            return new Element("cube", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces);
        }

        private static Element cubeColumn() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.simple("#end"));
            faces.put(Direction.UP, Face.simple("#end"));
            faces.put(Direction.NORTH, Face.simple("#side"));
            faces.put(Direction.SOUTH, Face.simple("#side"));
            faces.put(Direction.WEST, Face.simple("#side"));
            faces.put(Direction.EAST, Face.simple("#side"));
            return new Element("cube_column", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces);
        }

        private static Element cubeBottomTop() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.simple("#bottom"));
            faces.put(Direction.UP, Face.simple("#top"));
            faces.put(Direction.NORTH, Face.simple("#side"));
            faces.put(Direction.SOUTH, Face.simple("#side"));
            faces.put(Direction.WEST, Face.simple("#side"));
            faces.put(Direction.EAST, Face.simple("#side"));
            return new Element("cube_bottom_top", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces);
        }

        private static Element orientable() {
            EnumMap<Direction, Face> faces = new EnumMap<>(Direction.class);
            faces.put(Direction.DOWN, Face.simple("#bottom"));
            faces.put(Direction.UP, Face.simple("#top"));
            faces.put(Direction.NORTH, Face.simple("#front"));
            faces.put(Direction.SOUTH, Face.simple("#side"));
            faces.put(Direction.WEST, Face.simple("#side"));
            faces.put(Direction.EAST, Face.simple("#side"));
            return new Element("orientable", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F, Rotation.NONE, faces);
        }

        private static List<Element> cross() {
            List<Element> elements = new ArrayList<>(2);
            EnumMap<Direction, Face> firstFaces = new EnumMap<>(Direction.class);
            firstFaces.put(Direction.WEST, Face.simple("#cross"));
            firstFaces.put(Direction.EAST, Face.simple("#cross"));
            elements.add(new Element("cross_0", 7.5F, 0.0F, -3.3137F, 8.5F, 16.0F, 19.3137F, Rotation.of("y", 45.0F, 8.0F, 8.0F, 8.0F), firstFaces));

            EnumMap<Direction, Face> secondFaces = new EnumMap<>(Direction.class);
            secondFaces.put(Direction.WEST, Face.simple("#cross"));
            secondFaces.put(Direction.EAST, Face.simple("#cross"));
            elements.add(new Element("cross_1", 7.5F, 0.0F, -3.3137F, 8.5F, 16.0F, 19.3137F, Rotation.of("y", -45.0F, 8.0F, 8.0F, 8.0F), secondFaces));
            return elements;
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

        private Face(float[] uv, String texture, int rotation, Direction cullFace) {
            this.uv = uv;
            this.texture = texture;
            this.rotation = rotation;
            this.cullFace = cullFace;
        }

        public static Face fromJson(JsonObject object) {
            float[] uv = object.has("uv") && object.get("uv").isJsonArray()
                    ? parseFaceUv(object.getAsJsonArray("uv"))
                    : null;
            String texture = object.has("texture") ? object.get("texture").getAsString() : null;
            int rotation = object.has("rotation") ? object.get("rotation").getAsInt() : 0;
            Direction cullFace = object.has("cullface") ? Direction.fromName(object.get("cullface").getAsString()) : null;
            return new Face(uv, texture, rotation, cullFace);
        }

        private static Face simple(String texture) {
            return new Face(null, texture, 0, null);
        }

        public float[] getUv(float fromX, float fromY, float fromZ, float toX, float toY, float toZ, Direction direction, int textureWidth, int textureHeight) {
            if (this.uv != null) {
                return this.uv;
            }

            return switch (direction) {
                case DOWN, UP -> new float[]{fromX, fromZ, toX, toZ};
                case NORTH, SOUTH -> new float[]{fromX, 16.0F - toY, toX, 16.0F - fromY};
                case WEST, EAST -> new float[]{fromZ, 16.0F - toY, toZ, 16.0F - fromY};
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

        private static float[] parseFaceUv(JsonArray array) {
            if (array.size() < 4) {
                return null;
            }

            return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat(), array.get(3).getAsFloat()};
        }
    }

    public static final class Rotation {
        public static final Rotation NONE = new Rotation("", 0.0F, new float[]{8.0F, 8.0F, 8.0F});

        private final String axis;
        private final float angle;
        private final float[] origin;

        private Rotation(String axis, float angle, float[] origin) {
            this.axis = axis;
            this.angle = angle;
            this.origin = origin;
        }

        public static Rotation fromJson(JsonObject object) {
            String axis = object.has("axis") ? object.get("axis").getAsString().toLowerCase(Locale.ROOT) : "";
            float angle = object.has("angle") ? object.get("angle").getAsFloat() : 0.0F;
            float[] origin = object.has("origin") && object.get("origin").isJsonArray()
                    ? Element.parseVector(object.getAsJsonArray("origin"), 8.0F, 8.0F, 8.0F)
                    : new float[]{8.0F, 8.0F, 8.0F};
            return new Rotation(axis, angle, origin);
        }

        public static Rotation of(String axis, float angle, float originX, float originY, float originZ) {
            return new Rotation(axis, angle, new float[]{originX, originY, originZ});
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

        private DisplayTransform(float[] rotation, float[] translation, float[] scale) {
            this.rotation = rotation;
            this.translation = translation;
            this.scale = scale;
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
            return new DisplayTransform(rotation, translation, scale);
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
        GUI("gui"),
        GROUND("ground"),
        FIXED("fixed"),
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
