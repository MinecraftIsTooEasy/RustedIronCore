package moddedmite.rustedironcore.api.chat;

import moddedmite.rustedironcore.internal.chat.ChatClickSupport;
import net.minecraft.ChatMessageComponent;
import net.minecraft.EnumChatFormatting;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * Creates chat components with the standard {@code clickEvent/open_url} action.
 */
public final class ChatLinks {

    /**
     * Creates a blue, underlined component that opens the supplied HTTP(S) URL.
     */
    public static ChatMessageComponent createOpenUrl(String text, String url) {
        return withOpenUrl(ChatMessageComponent.createFromText(text)
                .setColor(EnumChatFormatting.BLUE)
                .setUnderline(true), url);
    }

    /**
     * Makes the whole component open the supplied HTTP(S) URL when clicked.
     */
    public static ChatMessageComponent withOpenUrl(ChatMessageComponent component, String url) {
        Objects.requireNonNull(component, "component");
        URI uri = ChatClickSupport.parseHttpUrl(url);
        if (uri == null) {
            throw new IllegalArgumentException("Only valid HTTP and HTTPS URLs can be opened: " + url);
        }
        ChatClickEvents.set(component, ChatClickAction.OPEN_URL, uri.toASCIIString());
        return component;
    }

    /**
     * Returns the URL assigned directly to this component, if present.
     */
    public static Optional<String> getOpenUrl(ChatMessageComponent component) {
        return ChatClickEvents.get(component)
                .filter(event -> event.action() == ChatClickAction.OPEN_URL)
                .map(ChatClickEvent::value);
    }
}
