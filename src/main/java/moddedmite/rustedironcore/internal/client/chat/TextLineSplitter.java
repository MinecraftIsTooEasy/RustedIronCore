package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public class TextLineSplitter {

    private final ToIntFunction<String> widthProvider;

    public TextLineSplitter(ToIntFunction<String> widthProvider) {
        this.widthProvider = widthProvider;
    }

    public int stringWidth(String text) {
        int width = 0;
        String formatting = "";
        int formattingWidth = this.widthProvider.applyAsInt(formatting);
        for (int index = 0; index < text.length();) {
            if (text.charAt(index) == LegacyTextFormatting.CODE && index + 1 < text.length()) {
                int length = LegacyTextFormatting.isRgbCodeAt(text, index) ? 14 : 2;
                formatting = LegacyTextFormatting.activeFormatting(formatting + text.substring(index, index + length));
                formattingWidth = this.widthProvider.applyAsInt(formatting);
                index += length;
            } else {
                int codePoint = text.codePointAt(index);
                String character = new String(Character.toChars(codePoint));
                width += this.widthProvider.applyAsInt(formatting + character) - formattingWidth;
                index += Character.charCount(codePoint);
            }
        }
        return width;
    }

    public List<String> splitLines(TextComponent component, int maxWidth) {
        StringBuilder formatted = new StringBuilder();
        MiteTextComponentAdapter.visit(component, (style, text) -> {
            if (!text.isEmpty()) formatted.append(MiteTextComponentAdapter.format(text, style));
        });
        String text = formatted.toString();
        List<String> lines = new ArrayList<>();
        int limit = Math.max(maxWidth, 1);
        int start = 0;
        String lineFormatting = "";
        boolean endedWithNewline = false;
        while (start < text.length()) {
            int index = start;
            int lastSpace = -1;
            String formatting = lineFormatting;
            String lastSpaceFormatting = "";
            int formattingWidth = this.widthProvider.applyAsInt(formatting);
            int width = 0;
            boolean hadNonZeroWidthCharacter = false;
            boolean lineBreakFound = false;
            while (index < text.length()) {
                if (text.charAt(index) == LegacyTextFormatting.CODE && index + 1 < text.length()) {
                    int length = LegacyTextFormatting.isRgbCodeAt(text, index) ? 14 : 2;
                    formatting = LegacyTextFormatting.activeFormatting(formatting + text.substring(index, index + length));
                    formattingWidth = this.widthProvider.applyAsInt(formatting);
                    index += length;
                    continue;
                }
                int codePoint = text.codePointAt(index);
                if (codePoint == '\n') {
                    lines.add(lineFormatting + text.substring(start, index));
                    start = index + 1;
                    lineFormatting = formatting;
                    endedWithNewline = true;
                    lineBreakFound = true;
                    break;
                }
                if (codePoint == ' ') {
                    lastSpace = index;
                    lastSpaceFormatting = formatting;
                }
                String character = new String(Character.toChars(codePoint));
                int characterWidth = this.widthProvider.applyAsInt(formatting + character) - formattingWidth;
                width += characterWidth;
                if (hadNonZeroWidthCharacter && width > limit) {
                    int split = lastSpace >= 0 ? lastSpace : index;
                    lines.add(lineFormatting + text.substring(start, split));
                    start = split + (text.charAt(split) == ' ' ? 1 : 0);
                    lineFormatting = lastSpace >= 0 ? lastSpaceFormatting : formatting;
                    endedWithNewline = false;
                    lineBreakFound = true;
                    break;
                }
                hadNonZeroWidthCharacter |= characterWidth != 0;
                index += Character.charCount(codePoint);
            }
            if (!lineBreakFound) {
                lines.add(lineFormatting + text.substring(start));
                endedWithNewline = false;
                break;
            }
        }
        if (endedWithNewline) lines.add("");
        return lines;
    }
}
