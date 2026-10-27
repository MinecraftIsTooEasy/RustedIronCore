package moddedmite.rustedironcore.api.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickComponentAccess;
import moddedmite.rustedironcore.internal.server.chat.TextComponentServerResolver;
import net.minecraft.ChatMessageComponent;
import net.minecraft.Entity;
import net.minecraft.ICommandSender;

import javax.annotation.Nullable;
import java.util.Optional;

public final class ChatComponents {

    public static ChatMessageComponent create(TextComponent text) {
        return setStructured(ChatMessageComponent.createFromText(""), text);
    }

    public static ChatMessageComponent fromJson(String json) {
        return ChatMessageComponent.createFromJson(json);
    }

    public static String toJson(ChatMessageComponent component) {
        return component.toJson();
    }

    public static TextComponent resolveFor(TextComponent component, ICommandSender source, @Nullable Entity target) {
        return TextComponentServerResolver.resolve(component, source, target);
    }

    public static Optional<TextComponent> getStructured(ChatMessageComponent component) {
        return Optional.ofNullable(((ChatClickComponentAccess) component).ric$getTextComponent());
    }

    public static ChatMessageComponent setStructured(ChatMessageComponent component, @Nullable TextComponent text) {
        ((ChatClickComponentAccess) component).ric$setTextComponent(text);
        return component;
    }
}
