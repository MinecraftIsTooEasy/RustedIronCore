package moddedmite.rustedironcore.client.api.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;

import java.util.Objects;

/**
 * A click event covering a half-open range of visible chat characters.
 */
public record ChatClickTextRange(int start, int end, ChatClickEvent event) {

    public ChatClickTextRange {
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("Invalid chat click range: " + start + ".." + end);
        }
        Objects.requireNonNull(event, "event");
    }
}
