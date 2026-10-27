package moddedmite.rustedironcore.api.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Objects;

public record TextHoverEvent(Action action, JsonElement contents) {

    public TextHoverEvent {
        Objects.requireNonNull(action, "action");
        contents = new JsonParser().parse(Objects.requireNonNull(contents, "contents").toString());
    }

    public enum Action {
        SHOW_TEXT("show_text"),
        SHOW_ITEM("show_item"),
        SHOW_ENTITY("show_entity");

        private final String serializedName;

        Action(String serializedName) {
            this.serializedName = serializedName;
        }

        public String getSerializedName() {
            return this.serializedName;
        }

        public static Action fromSerializedName(String name) {
            for (Action action : values()) {
                if (action.serializedName.equals(name)) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Unknown hover action: " + name);
        }
    }
}
