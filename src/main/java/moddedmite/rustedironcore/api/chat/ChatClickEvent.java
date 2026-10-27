package moddedmite.rustedironcore.api.chat;

import java.util.Objects;

/**
 * A Minecraft-compatible chat click action and its payload.
 */
public record ChatClickEvent(ChatClickAction action, String value) {

    public ChatClickEvent {
        Objects.requireNonNull(action, "action");
        Objects.requireNonNull(value, "value");
    }
}
