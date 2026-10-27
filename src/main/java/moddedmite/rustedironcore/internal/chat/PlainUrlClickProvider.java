package moddedmite.rustedironcore.internal.chat;

import moddedmite.rustedironcore.api.chat.ChatClickAction;
import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.client.api.chat.ChatClickTextRange;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PlainUrlClickProvider {

    private static final Pattern PLAIN_URL = Pattern.compile(
            "(?i)(?<![-\\w.@])(?:(?:https?://)?(?:[-\\w]+\\.)+[a-z]{2,63})(?::\\d+)?(?:/\\S*)?"
    );

    static List<ChatClickTextRange> findClickTargets(String plainText) {
        Matcher matcher = PLAIN_URL.matcher(plainText);
        List<ChatClickTextRange> ranges = new ArrayList<>();
        while (matcher.find()) {
            int end = matcher.end();
            while (end > matcher.start() && isTrailingPunctuation(plainText.charAt(end - 1))) {
                end--;
            }
            if (end > matcher.start()) {
                String url = plainText.substring(matcher.start(), end);
                if (!url.regionMatches(true, 0, "http://", 0, 7)
                        && !url.regionMatches(true, 0, "https://", 0, 8)) {
                    url = "http://" + url;
                }
                ranges.add(new ChatClickTextRange(
                        matcher.start(),
                        end,
                        new ChatClickEvent(ChatClickAction.OPEN_URL, url)
                ));
            }
        }
        return ranges.isEmpty() ? List.of() : List.copyOf(ranges);
    }

    private static boolean isTrailingPunctuation(char character) {
        return character == '.' || character == ',' || character == ';' || character == ':'
                || character == '!' || character == '?' || character == ')' || character == ']';
    }
}
