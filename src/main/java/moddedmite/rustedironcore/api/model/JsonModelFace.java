package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class JsonModelFace {

    private final float[] uv;
    private final String texture;
    private final int rotation;
    private final JsonModelDirection cullFace;
    private final int tintIndex;

    private JsonModelFace(float[] uv, String texture, int rotation, JsonModelDirection cullFace, int tintIndex) {
        this.uv = uv;
        this.texture = texture;
        this.rotation = rotation;
        this.cullFace = cullFace;
        this.tintIndex = tintIndex;
    }

    public static JsonModelFace fromJson(JsonObject object) {
        float[] uv = object.has("uv") && object.get("uv").isJsonArray()
                ? parseFaceUv(object.getAsJsonArray("uv"))
                : null;
        String texture = object.has("texture") ? object.get("texture").getAsString() : null;
        int rotation = object.has("rotation") ? object.get("rotation").getAsInt() : 0;
        JsonModelDirection cullFace = object.has("cullface")
                ? JsonModelDirection.fromName(object.get("cullface").getAsString()) : null;
        int tintIndex = object.has("tintindex") ? object.get("tintindex").getAsInt() : -1;
        return new JsonModelFace(uv, texture, rotation, cullFace, tintIndex);
    }

    static JsonModelFace simple(String texture) {
        return new JsonModelFace(null, texture, 0, null, -1);
    }

    static JsonModelFace tinted(String texture, int tintIndex) {
        return new JsonModelFace(null, texture, 0, null, tintIndex);
    }

    static JsonModelFace cull(String texture, JsonModelDirection cullFace) {
        return new JsonModelFace(null, texture, 0, cullFace, -1);
    }

    static JsonModelFace cull(String texture, JsonModelDirection cullFace, int rotation) {
        return new JsonModelFace(null, texture, rotation, cullFace, -1);
    }

    static JsonModelFace uv(String texture, float minU, float minV, float maxU, float maxV) {
        return new JsonModelFace(new float[]{minU, minV, maxU, maxV}, texture, 0, null, -1);
    }

    static JsonModelFace cull(String texture, float minU, float minV, float maxU, float maxV,
                              JsonModelDirection cullFace) {
        return new JsonModelFace(new float[]{minU, minV, maxU, maxV}, texture, 0, cullFace, -1);
    }

    public float[] getUv(float fromX, float fromY, float fromZ, float toX, float toY, float toZ,
                         JsonModelDirection direction, int textureWidth, int textureHeight) {
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

    public JsonModelDirection getCullFace() {
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
