package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class JsonDisplayTransform {

    private final float[] rotation;
    private final float[] translation;
    private final float[] scale;
    private final float[] rightRotation;

    private JsonDisplayTransform(float[] rotation, float[] translation, float[] scale, float[] rightRotation) {
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
        this.rightRotation = rightRotation;
    }

    public static JsonDisplayTransform fromJson(JsonObject object) {
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
        return new JsonDisplayTransform(rotation, translation, scale, rightRotation);
    }

    static JsonDisplayTransform of(float rotationX, float rotationY, float rotationZ,
                                   float translationX, float translationY, float translationZ,
                                   float scaleX, float scaleY, float scaleZ) {
        return new JsonDisplayTransform(
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
