package moddedmite.rustedironcore.api.model;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public final class JsonDisplayTransforms {

    static final JsonDisplayTransforms EMPTY = new JsonDisplayTransforms(new HashMap<>());

    private final Map<JsonModelDisplayContext, JsonDisplayTransform> transforms;

    private JsonDisplayTransforms(Map<JsonModelDisplayContext, JsonDisplayTransform> transforms) {
        this.transforms = transforms;
    }

    public static JsonDisplayTransforms fromJson(JsonObject object, JsonDisplayTransforms parent) {
        Map<JsonModelDisplayContext, JsonDisplayTransform> transforms = new HashMap<>(parent.transforms);
        for (JsonModelDisplayContext context : JsonModelDisplayContext.values()) {
            String key = context.getJsonName();
            if (key != null && object.has(key) && object.get(key).isJsonObject()) {
                transforms.put(context, JsonDisplayTransform.fromJson(object.getAsJsonObject(key)));
            }
        }
        return new JsonDisplayTransforms(transforms);
    }

    static JsonDisplayTransforms blockDefaults(JsonDisplayTransforms parent) {
        Map<JsonModelDisplayContext, JsonDisplayTransform> transforms = new HashMap<>(parent.transforms);
        transforms.putIfAbsent(JsonModelDisplayContext.GUI, JsonDisplayTransform.of(30.0F, 225.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F, 0.625F, 0.625F));
        transforms.putIfAbsent(JsonModelDisplayContext.GROUND, JsonDisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 0.0F, 0.25F, 0.25F, 0.25F));
        transforms.putIfAbsent(JsonModelDisplayContext.FIXED, JsonDisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F));
        transforms.putIfAbsent(JsonModelDisplayContext.THIRD_PERSON_RIGHT_HAND, JsonDisplayTransform.of(75.0F, 45.0F, 0.0F, 0.0F, 2.5F, 0.0F, 0.375F, 0.375F, 0.375F));
        transforms.putIfAbsent(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND, JsonDisplayTransform.of(0.0F, 45.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F, 0.4F, 0.4F));
        transforms.putIfAbsent(JsonModelDisplayContext.FIRST_PERSON_LEFT_HAND, JsonDisplayTransform.of(0.0F, 225.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.4F, 0.4F, 0.4F));
        return new JsonDisplayTransforms(transforms);
    }

    static JsonDisplayTransforms generatedItemDefaults(JsonDisplayTransforms parent) {
        Map<JsonModelDisplayContext, JsonDisplayTransform> transforms = new HashMap<>(parent.transforms);
        transforms.putIfAbsent(JsonModelDisplayContext.GUI, JsonDisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        transforms.putIfAbsent(JsonModelDisplayContext.GROUND, JsonDisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 0.5F, 0.5F, 0.5F));
        transforms.putIfAbsent(JsonModelDisplayContext.HEAD, JsonDisplayTransform.of(0.0F, 180.0F, 0.0F, 0.0F, 13.0F, 7.0F, 1.0F, 1.0F, 1.0F));
        transforms.putIfAbsent(JsonModelDisplayContext.THIRD_PERSON_RIGHT_HAND, JsonDisplayTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 3.0F, 1.0F, 0.55F, 0.55F, 0.55F));
        transforms.putIfAbsent(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND, JsonDisplayTransform.of(0.0F, -90.0F, 25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
        transforms.putIfAbsent(JsonModelDisplayContext.FIXED, JsonDisplayTransform.of(0.0F, 180.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F));
        return new JsonDisplayTransforms(transforms);
    }

    static JsonDisplayTransforms handheldItemDefaults(JsonDisplayTransforms parent) {
        Map<JsonModelDisplayContext, JsonDisplayTransform> transforms = new HashMap<>(generatedItemDefaults(parent).transforms);
        transforms.put(JsonModelDisplayContext.THIRD_PERSON_RIGHT_HAND, JsonDisplayTransform.of(0.0F, -90.0F, 55.0F, 0.0F, 4.0F, 0.5F, 0.85F, 0.85F, 0.85F));
        transforms.put(JsonModelDisplayContext.THIRD_PERSON_LEFT_HAND, JsonDisplayTransform.of(0.0F, 90.0F, -55.0F, 0.0F, 4.0F, 0.5F, 0.85F, 0.85F, 0.85F));
        transforms.put(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND, JsonDisplayTransform.of(0.0F, -90.0F, 25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
        transforms.put(JsonModelDisplayContext.FIRST_PERSON_LEFT_HAND, JsonDisplayTransform.of(0.0F, 90.0F, -25.0F, 1.13F, 3.2F, 1.13F, 0.68F, 0.68F, 0.68F));
        return new JsonDisplayTransforms(transforms);
    }

    static JsonDisplayTransforms handheldRodItemDefaults(JsonDisplayTransforms parent) {
        return handheldItemDefaults(parent);
    }

    public JsonDisplayTransform get(JsonModelDisplayContext context) {
        JsonDisplayTransform transform = this.transforms.get(context);
        if (transform != null) {
            return transform;
        }
        if (context == JsonModelDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return this.transforms.get(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND);
        }
        if (context == JsonModelDisplayContext.THIRD_PERSON_LEFT_HAND) {
            return this.transforms.get(JsonModelDisplayContext.THIRD_PERSON_RIGHT_HAND);
        }
        return this.transforms.get(JsonModelDisplayContext.FIXED);
    }
}
