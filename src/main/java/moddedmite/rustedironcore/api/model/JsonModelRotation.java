package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonObject;

import java.util.Locale;

public final class JsonModelRotation {

    public static final JsonModelRotation NONE = new JsonModelRotation("", 0.0F, new float[]{8.0F, 8.0F, 8.0F}, false);

    private final String axis;
    private final float angle;
    private final float[] origin;
    private final boolean rescale;

    private JsonModelRotation(String axis, float angle, float[] origin, boolean rescale) {
        this.axis = axis;
        this.angle = angle;
        this.origin = origin;
        this.rescale = rescale;
    }

    public static JsonModelRotation fromJson(JsonObject object) {
        String axis = object.has("axis") ? object.get("axis").getAsString().toLowerCase(Locale.ROOT) : "";
        float angle = object.has("angle") ? object.get("angle").getAsFloat() : 0.0F;
        float[] origin = object.has("origin") && object.get("origin").isJsonArray()
                ? JsonModelElement.parseVector(object.getAsJsonArray("origin"), 8.0F, 8.0F, 8.0F)
                : new float[]{8.0F, 8.0F, 8.0F};
        boolean rescale = object.has("rescale") && object.get("rescale").getAsBoolean();
        return new JsonModelRotation(axis, angle, origin, rescale);
    }

    public static JsonModelRotation of(String axis, float angle, float originX, float originY, float originZ) {
        return new JsonModelRotation(axis, angle, new float[]{originX, originY, originZ}, false);
    }

    public static JsonModelRotation of(String axis, float angle, float originX, float originY, float originZ, boolean rescale) {
        return new JsonModelRotation(axis, angle, new float[]{originX, originY, originZ}, rescale);
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
