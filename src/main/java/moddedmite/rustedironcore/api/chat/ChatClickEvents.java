package moddedmite.rustedironcore.api.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import net.minecraft.ChatMessageComponent;

import java.util.Objects;
import java.util.Optional;

/**
 * Assigns Minecraft-compatible click events to chat components.
 */
public final class ChatClickEvents {

    public static ChatMessageComponent set(ChatMessageComponent component, ChatClickAction action, String value) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setClickEvent(new ChatClickEvent(action, value));
        return component;
    }

    public static Optional<ChatClickEvent> get(ChatMessageComponent component) {
        if (component == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(((ChatClickComponentAccess) component).ric$getClickEvent());
    }

    public static ChatMessageComponent clear(ChatMessageComponent component) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setClickEvent(null);
        return component;
    }
}
