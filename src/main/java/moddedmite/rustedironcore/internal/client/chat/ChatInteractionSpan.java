package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;

import javax.annotation.Nullable;

public record ChatInteractionSpan(int start, int end, @Nullable ChatClickEvent clickEvent, @Nullable String insertion, @Nullable TextHoverEvent hoverEvent) {

    public ChatInteractionSpan {
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("Invalid chat interaction range: " + start + ".." + end);
        }
        if (clickEvent == null && insertion == null && hoverEvent == null) {
            throw new IllegalArgumentException("A chat interaction span needs a click event, insertion, or hover event");
        }
    }
}
