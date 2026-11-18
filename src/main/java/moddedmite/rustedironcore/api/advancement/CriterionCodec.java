package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonObject;

import java.util.function.Function;

/** Converts a trigger's JSON conditions to and from its typed instance. */
public record CriterionCodec<T>(Function<JsonObject, T> decoder, Function<T, JsonObject> encoder) {

    public static final CriterionCodec<JsonObject> JSON = new CriterionCodec<>(value -> value, value -> value);

    public T decode(JsonObject json) {
        return decoder.apply(json);
    }

    public JsonObject encode(T instance) {
        return encoder.apply(instance);
    }
}
