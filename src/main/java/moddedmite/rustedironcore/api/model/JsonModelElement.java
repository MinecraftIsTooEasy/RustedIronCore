package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class JsonModelElement {

    private final String name;
    private final float fromX;
    private final float fromY;
    private final float fromZ;
    private final float toX;
    private final float toY;
    private final float toZ;
    private final JsonModelRotation rotation;
    private final EnumMap<JsonModelDirection, JsonModelFace> faces;
    private final boolean shade;

    private JsonModelElement(String name, float fromX, float fromY, float fromZ,
                             float toX, float toY, float toZ, JsonModelRotation rotation,
                             EnumMap<JsonModelDirection, JsonModelFace> faces, boolean shade) {
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

    public static JsonModelElement fromJson(JsonObject object) {
        float[] from = parseVector(object.getAsJsonArray("from"), 0.0F, 0.0F, 0.0F);
        float[] to = parseVector(object.getAsJsonArray("to"), 16.0F, 16.0F, 16.0F);
        JsonModelRotation rotation = object.has("rotation") && object.get("rotation").isJsonObject()
                ? JsonModelRotation.fromJson(object.getAsJsonObject("rotation"))
                : JsonModelRotation.NONE;
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);

        if (object.has("faces") && object.get("faces").isJsonObject()) {
            JsonObject faceObject = object.getAsJsonObject("faces");
            for (Map.Entry<String, JsonElement> entry : faceObject.entrySet()) {
                JsonModelDirection direction = JsonModelDirection.fromName(entry.getKey());
                if (direction != null && entry.getValue().isJsonObject()) {
                    faces.put(direction, JsonModelFace.fromJson(entry.getValue().getAsJsonObject()));
                }
            }
        }

        String name = object.has("name") ? object.get("name").getAsString() : "";
        boolean shade = !object.has("shade") || object.get("shade").getAsBoolean();
        return new JsonModelElement(name, from[0], from[1], from[2], to[0], to[1], to[2],
                rotation, faces, shade);
    }

    static JsonModelElement cubeAll() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        for (JsonModelDirection direction : JsonModelDirection.values()) {
            faces.put(direction, JsonModelFace.cull("#all", direction));
        }
        return new JsonModelElement("cube_all", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static JsonModelElement cube() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.cull("#down", JsonModelDirection.DOWN));
        faces.put(JsonModelDirection.UP, JsonModelFace.cull("#up", JsonModelDirection.UP));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.cull("#north", JsonModelDirection.NORTH));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.cull("#south", JsonModelDirection.SOUTH));
        faces.put(JsonModelDirection.WEST, JsonModelFace.cull("#west", JsonModelDirection.WEST));
        faces.put(JsonModelDirection.EAST, JsonModelFace.cull("#east", JsonModelDirection.EAST));
        return new JsonModelElement("cube", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static JsonModelElement cubeColumn() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.cull("#end", JsonModelDirection.DOWN));
        faces.put(JsonModelDirection.UP, JsonModelFace.cull("#end", JsonModelDirection.UP));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.cull("#side", JsonModelDirection.NORTH));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.cull("#side", JsonModelDirection.SOUTH));
        faces.put(JsonModelDirection.WEST, JsonModelFace.cull("#side", JsonModelDirection.WEST));
        faces.put(JsonModelDirection.EAST, JsonModelFace.cull("#side", JsonModelDirection.EAST));
        return new JsonModelElement("cube_column", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static JsonModelElement cubeColumnHorizontal() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.cull("#end", JsonModelDirection.DOWN));
        faces.put(JsonModelDirection.UP, JsonModelFace.cull("#end", JsonModelDirection.UP, 180));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.cull("#side", JsonModelDirection.NORTH));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.cull("#side", JsonModelDirection.SOUTH));
        faces.put(JsonModelDirection.WEST, JsonModelFace.cull("#side", JsonModelDirection.WEST));
        faces.put(JsonModelDirection.EAST, JsonModelFace.cull("#side", JsonModelDirection.EAST));
        return new JsonModelElement("cube_column_horizontal", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static JsonModelElement cubeBottomTop() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.cull("#bottom", JsonModelDirection.DOWN));
        faces.put(JsonModelDirection.UP, JsonModelFace.cull("#top", JsonModelDirection.UP));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.cull("#side", JsonModelDirection.NORTH));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.cull("#side", JsonModelDirection.SOUTH));
        faces.put(JsonModelDirection.WEST, JsonModelFace.cull("#side", JsonModelDirection.WEST));
        faces.put(JsonModelDirection.EAST, JsonModelFace.cull("#side", JsonModelDirection.EAST));
        return new JsonModelElement("cube_bottom_top", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static JsonModelElement orientable() {
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.simple("#bottom"));
        faces.put(JsonModelDirection.UP, JsonModelFace.simple("#top"));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.simple("#front"));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.simple("#side"));
        faces.put(JsonModelDirection.WEST, JsonModelFace.simple("#side"));
        faces.put(JsonModelDirection.EAST, JsonModelFace.simple("#side"));
        return new JsonModelElement("orientable", 0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F,
                JsonModelRotation.NONE, faces, true);
    }

    static List<JsonModelElement> cross() {
        List<JsonModelElement> elements = new ArrayList<>(2);
        EnumMap<JsonModelDirection, JsonModelFace> firstFaces = new EnumMap<>(JsonModelDirection.class);
        firstFaces.put(JsonModelDirection.NORTH, JsonModelFace.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
        firstFaces.put(JsonModelDirection.SOUTH, JsonModelFace.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
        elements.add(new JsonModelElement("cross_0", 0.8F, 0.0F, 8.0F, 15.2F, 16.0F, 8.0F,
                JsonModelRotation.of("y", 45.0F, 8.0F, 8.0F, 8.0F, true), firstFaces, false));

        EnumMap<JsonModelDirection, JsonModelFace> secondFaces = new EnumMap<>(JsonModelDirection.class);
        secondFaces.put(JsonModelDirection.WEST, JsonModelFace.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
        secondFaces.put(JsonModelDirection.EAST, JsonModelFace.uv("#cross", 0.0F, 0.0F, 16.0F, 16.0F));
        elements.add(new JsonModelElement("cross_1", 8.0F, 0.0F, 0.8F, 8.0F, 16.0F, 15.2F,
                JsonModelRotation.of("y", 45.0F, 8.0F, 8.0F, 8.0F, true), secondFaces, false));
        return elements;
    }

    static List<JsonModelElement> tintedCross() {
        List<JsonModelElement> elements = cross();
        List<JsonModelElement> tinted = new ArrayList<>(elements.size());
        for (JsonModelElement element : elements) {
            EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
            for (Map.Entry<JsonModelDirection, JsonModelFace> entry : element.faces.entrySet()) {
                faces.put(entry.getKey(), JsonModelFace.tinted(entry.getValue().getTexture(), 0));
            }
            tinted.add(new JsonModelElement(element.name, element.fromX, element.fromY, element.fromZ,
                    element.toX, element.toY, element.toZ, element.rotation, faces, element.shade));
        }
        return tinted;
    }

    static List<JsonModelElement> crop() {
        List<JsonModelElement> elements = new ArrayList<>(4);
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.WEST, JsonModelFace.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
        faces.put(JsonModelDirection.EAST, JsonModelFace.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
        elements.add(new JsonModelElement("crop_west", 4.0F, -1.0F, 0.0F, 4.0F, 15.0F, 16.0F,
                JsonModelRotation.NONE, faces, false));

        faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.WEST, JsonModelFace.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
        faces.put(JsonModelDirection.EAST, JsonModelFace.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
        elements.add(new JsonModelElement("crop_east", 12.0F, -1.0F, 0.0F, 12.0F, 15.0F, 16.0F,
                JsonModelRotation.NONE, faces, false));

        faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.NORTH, JsonModelFace.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
        elements.add(new JsonModelElement("crop_north", 0.0F, -1.0F, 4.0F, 16.0F, 15.0F, 4.0F,
                JsonModelRotation.NONE, faces, false));

        faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.NORTH, JsonModelFace.uv("#crop", 16.0F, 0.0F, 0.0F, 16.0F));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.uv("#crop", 0.0F, 0.0F, 16.0F, 16.0F));
        elements.add(new JsonModelElement("crop_south", 0.0F, -1.0F, 12.0F, 16.0F, 15.0F, 12.0F,
                JsonModelRotation.NONE, faces, false));
        return elements;
    }

    static JsonModelElement slab(boolean top) {
        float fromY = top ? 8.0F : 0.0F;
        float toY = top ? 16.0F : 8.0F;
        EnumMap<JsonModelDirection, JsonModelFace> faces = new EnumMap<>(JsonModelDirection.class);
        faces.put(JsonModelDirection.DOWN, JsonModelFace.cull("#bottom", 0.0F, 0.0F, 16.0F, 16.0F,
                top ? null : JsonModelDirection.DOWN));
        faces.put(JsonModelDirection.UP, JsonModelFace.cull("#top", 0.0F, 0.0F, 16.0F, 16.0F,
                top ? JsonModelDirection.UP : null));
        faces.put(JsonModelDirection.NORTH, JsonModelFace.cull("#side", 0.0F, top ? 0.0F : 8.0F,
                16.0F, top ? 8.0F : 16.0F, JsonModelDirection.NORTH));
        faces.put(JsonModelDirection.SOUTH, JsonModelFace.cull("#side", 0.0F, top ? 0.0F : 8.0F,
                16.0F, top ? 8.0F : 16.0F, JsonModelDirection.SOUTH));
        faces.put(JsonModelDirection.WEST, JsonModelFace.cull("#side", 0.0F, top ? 0.0F : 8.0F,
                16.0F, top ? 8.0F : 16.0F, JsonModelDirection.WEST));
        faces.put(JsonModelDirection.EAST, JsonModelFace.cull("#side", 0.0F, top ? 0.0F : 8.0F,
                16.0F, top ? 8.0F : 16.0F, JsonModelDirection.EAST));
        return new JsonModelElement(top ? "slab_top" : "slab", 0.0F, fromY, 0.0F,
                16.0F, toY, 16.0F, JsonModelRotation.NONE, faces, true);
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

    public JsonModelRotation getRotation() {
        return this.rotation;
    }

    public JsonModelFace getFace(JsonModelDirection direction) {
        return this.faces.get(direction);
    }

    public boolean isShade() {
        return this.shade;
    }

    static float[] parseVector(JsonArray array, float x, float y, float z) {
        if (array == null || array.size() < 3) {
            return new float[]{x, y, z};
        }
        return new float[]{array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat()};
    }
}
