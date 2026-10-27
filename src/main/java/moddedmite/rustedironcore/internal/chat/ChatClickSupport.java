package moddedmite.rustedironcore.internal.chat;

import moddedmite.rustedironcore.api.chat.ChatClickAction;
import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;
import moddedmite.rustedironcore.client.api.chat.ChatClickTextProviders;
import moddedmite.rustedironcore.client.api.chat.ChatClickTextRange;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionSpan;
import net.minecraft.ChatMessageComponent;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

public final class ChatClickSupport {

    @Nullable
    public static URI parseHttpUrl(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            String normalized = value.trim();
            if (!normalized.regionMatches(true, 0, "http://", 0, 7)
                    && !normalized.regionMatches(true, 0, "https://", 0, 8)) {
                normalized = "http://" + normalized;
            }
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (scheme == null || uri.getHost() == null) {
                return null;
            }
            scheme = scheme.toLowerCase(Locale.ROOT);
            return "http".equals(scheme) || "https".equals(scheme) ? uri : null;
        } catch (URISyntaxException exception) {
            return null;
        }
    }

    public static URI parseUntrustedHttpUrl(String value) throws URISyntaxException {
        URI uri = new URI(value);
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new URISyntaxException(value, "Missing protocol in URI");
        }
        String normalizedScheme = scheme.toLowerCase(Locale.ROOT);
        if (!"http".equals(normalizedScheme) && !"https".equals(normalizedScheme)) {
            throw new URISyntaxException(value, "Unsupported protocol in URI: " + scheme);
        }
        return uri;
    }

    public static List<ChatInteractionSpan> createPlainTextInteractionSpans(String formattedText) {
        String plainText = stripFormatting(formattedText);
        List<ChatClickTextRange> ranges = new ArrayList<>(PlainUrlClickProvider.findClickTargets(plainText));
        ranges.addAll(ChatClickTextProviders.findClickTargets(plainText));
        if (ranges.isEmpty()) {
            return Collections.emptyList();
        }
        List<ChatInteractionSpan> spans = new ArrayList<>(ranges.size());
        for (ChatClickTextRange range : ranges) {
            spans.add(new ChatInteractionSpan(range.start(), range.end(), range.event(), null, null));
        }
        return List.copyOf(spans);
    }

    public static boolean hasChatInteraction(ChatMessageComponent component) {
        ChatClickComponentAccess access = (ChatClickComponentAccess) component;
        if (access.ric$getClickEvent() != null || access.ric$getInsertion() != null || access.ric$getHoverEvent() != null) {
            return true;
        }
        List<?> children = access.ric$getChildren();
        if (children != null) {
            for (Object child : children) {
                if (child instanceof ChatMessageComponent childComponent && hasChatInteraction(childComponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasTextComponent(ChatMessageComponent component) {
        return ((ChatClickComponentAccess) component).ric$getTextComponent() != null;
    }

    public static boolean removeClickEventsNotAllowedFromServer(ChatMessageComponent component) {
        ChatClickComponentAccess access = (ChatClickComponentAccess) component;
        ChatClickEvent event = access.ric$getClickEvent();
        boolean removed = event != null && !event.action().isAllowedFromServer();
        if (removed) {
            access.ric$setClickEvent(null);
        }
        List<?> children = access.ric$getChildren();
        if (children == null) {
            return removed;
        }
        for (Object child : children) {
            if (child instanceof ChatMessageComponent childComponent) {
                removed |= removeClickEventsNotAllowedFromServer(childComponent);
            }
        }
        return removed;
    }

    public static Deque<List<ChatInteractionSpan>> createWrappedLineInteractionSpans(ChatMessageComponent component,
                                                                                     List<?> wrappedLines) {
        String fullText = stripFormatting(component.toStringWithFormatting(true));
        List<ChatInteractionSpan> ranges = new ArrayList<>();
        collectRanges(component, 0, null, null, null, ranges);

        Deque<List<ChatInteractionSpan>> result = new ArrayDeque<>(wrappedLines.size());
        int searchFrom = 0;
        for (int lineIndex = 0; lineIndex < wrappedLines.size(); lineIndex++) {
            String lineText = stripFormatting((String) wrappedLines.get(lineIndex));
            int lineStart = fullText.indexOf(lineText, searchFrom);
            if (lineStart < 0) {
                lineStart = Math.min(searchFrom, fullText.length());
            }
            int lineEnd = Math.min(lineStart + lineText.length(), fullText.length());
            int storedLineOffset = lineIndex > 0 ? 1 : 0;
            List<ChatInteractionSpan> lineSpans = new ArrayList<>();
            for (ChatInteractionSpan range : ranges) {
                int start = Math.max(range.start(), lineStart);
                int end = Math.min(range.end(), lineEnd);
                if (start < end) {
                    lineSpans.add(new ChatInteractionSpan(
                            start - lineStart + storedLineOffset,
                            end - lineStart + storedLineOffset,
                            range.clickEvent(),
                            range.insertion(),
                            range.hoverEvent()
                    ));
                }
            }
            result.addLast(lineSpans.isEmpty() ? Collections.emptyList() : List.copyOf(lineSpans));
            searchFrom = lineEnd;
        }
        return result;
    }

    private static int collectRanges(ChatMessageComponent component, int offset,
                                     @Nullable ChatClickEvent inheritedEvent,
                                     @Nullable String inheritedInsertion,
                                     @Nullable TextHoverEvent inheritedHoverEvent,
                                     List<ChatInteractionSpan> ranges) {
        String text = stripFormatting(component.toStringWithFormatting(true));
        ChatClickComponentAccess access = (ChatClickComponentAccess) component;
        ChatClickEvent directEvent = access.ric$getClickEvent();
        ChatClickEvent effectiveEvent = directEvent == null ? inheritedEvent : directEvent;
        String directInsertion = access.ric$getInsertion();
        String effectiveInsertion = directInsertion == null ? inheritedInsertion : directInsertion;
        TextHoverEvent directHoverEvent = access.ric$getHoverEvent();
        TextHoverEvent effectiveHoverEvent = directHoverEvent == null ? inheritedHoverEvent : directHoverEvent;
        List<?> children = access.ric$getChildren();
        if (children == null || children.isEmpty()) {
            if ((effectiveEvent != null || effectiveInsertion != null || effectiveHoverEvent != null) && !text.isEmpty()) {
                ranges.add(new ChatInteractionSpan(offset, offset + text.length(), effectiveEvent, effectiveInsertion, effectiveHoverEvent));
            }
            return text.length();
        }

        if (access.ric$getText() == null && access.ric$getTranslationKey() == null) {
            int childOffset = offset;
            for (Object child : children) {
                if (child instanceof ChatMessageComponent childComponent) {
                    childOffset += collectRanges(childComponent, childOffset, effectiveEvent, effectiveInsertion, effectiveHoverEvent, ranges);
                }
            }
            return text.length();
        }

        int searchFrom = 0;
        for (Object child : children) {
            if (!(child instanceof ChatMessageComponent childComponent)) {
                continue;
            }
            String childText = stripFormatting(childComponent.toStringWithFormatting(true));
            int childStart = text.indexOf(childText, searchFrom);
            if (childStart >= 0) {
                collectRanges(childComponent, offset + childStart, effectiveEvent, effectiveInsertion, effectiveHoverEvent, ranges);
                searchFrom = childStart + childText.length();
            }
        }
        if ((effectiveEvent != null || effectiveInsertion != null || effectiveHoverEvent != null) && !text.isEmpty()) {
            ranges.add(new ChatInteractionSpan(offset, offset + text.length(), effectiveEvent, effectiveInsertion, effectiveHoverEvent));
        }
        return text.length();
    }

    private static String stripFormatting(String text) {
        return LegacyTextFormatting.strip(text);
    }
}
