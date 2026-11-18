package moddedmite.rustedironcore.api.advancement;

import com.google.gson.JsonElement;

/** Schema validation at data reload, followed by evaluation against native MITE state. */
public interface JsonPredicate<T> {

    void validate(JsonElement json);

    boolean matches(JsonElement json, T value);
}
