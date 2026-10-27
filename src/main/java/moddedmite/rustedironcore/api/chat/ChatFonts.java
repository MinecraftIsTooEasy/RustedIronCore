package moddedmite.rustedironcore.api.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import net.minecraft.ChatMessageComponent;

import java.util.Objects;
import java.util.Optional;

/**
 * Access to the modern text component font style on a MITE chat component.
 * The resource location is retained for serialization and style inheritance;
 * selecting a different glyph atlas is not implemented by the legacy renderer.
 */
public final class ChatFonts {

    public static ChatMessageComponent set(ChatMessageComponent component, String font) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setFont(Objects.requireNonNull(font, "font"));
        return component;
    }

    public static Optional<String> get(ChatMessageComponent component) {
        if (component == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(((ChatClickComponentAccess) component).ric$getFont());
    }

    public static ChatMessageComponent clear(ChatMessageComponent component) {
        Objects.requireNonNull(component, "component");
        ((ChatClickComponentAccess) component).ric$setFont(null);
        return component;
    }
}
