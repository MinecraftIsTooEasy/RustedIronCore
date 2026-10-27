package moddedmite.rustedironcore.internal.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TranslationTemplate {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public static List<Object> decompose(String template, List<?> arguments) {
        List<Object> result = new ArrayList<>();
        Matcher matcher = FORMAT_PATTERN.matcher(template);
        int implicitIndex = 0;
        int cursor = 0;

        while (matcher.find(cursor)) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > cursor) {
                addText(result, template.substring(cursor, start));
            }

            String conversion = matcher.group(2);
            String token = template.substring(start, end);
            if ("%".equals(conversion) && "%%".equals(token)) {
                addText(result, "%");
            } else if ("s".equals(conversion)) {
                String explicitIndex = matcher.group(1);
                int argumentIndex = explicitIndex == null ? implicitIndex++ : Integer.parseInt(explicitIndex) - 1;
                if (argumentIndex < 0 || argumentIndex >= arguments.size()) {
                    throw new IllegalArgumentException("Translation argument index out of range: " + argumentIndex);
                }
                result.add(arguments.get(argumentIndex));
            } else {
                throw new IllegalArgumentException("Unsupported translation format: " + token);
            }
            cursor = end;
        }

        if (cursor < template.length()) {
            String suffix = template.substring(cursor);
            if (suffix.indexOf('%') >= 0) {
                throw new IllegalArgumentException("Invalid translation format: " + suffix);
            }
            addText(result, suffix);
        }
        return List.copyOf(result);
    }

    private static void addText(List<Object> result, String text) {
        if (text.isEmpty()) {
            return;
        }
        int lastIndex = result.size() - 1;
        if (lastIndex >= 0 && result.get(lastIndex) instanceof String previous) {
            result.set(lastIndex, previous + text);
        } else {
            result.add(text);
        }
    }
}
