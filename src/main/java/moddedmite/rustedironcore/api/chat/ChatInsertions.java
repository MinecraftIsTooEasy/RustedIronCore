package moddedmite.rustedironcore.api.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import net.minecraft.ChatMessageComponent;

import java.util.Objects;
import java.util.Optional;

/**
 * Assigns the modern chat {@code insertion} style property to components.
 */
public final class ChatInsertions {

    public static ChatMessageComponent set(ChatMessageComponent component, String insertion) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setInsertion(Objects.requireNonNull(insertion, "insertion"));
        return component;
    }

    public static Optional<String> get(ChatMessageComponent component) {
        if (component == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(((ChatClickComponentAccess) component).ric$getInsertion());
    }

    public static ChatMessageComponent clear(ChatMessageComponent component) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setInsertion(null);
        return component;
    }
}
