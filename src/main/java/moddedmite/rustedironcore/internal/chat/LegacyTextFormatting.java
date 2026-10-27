package moddedmite.rustedironcore.internal.chat;

import java.util.Locale;

public final class LegacyTextFormatting {

    public static final char CODE = '\u00a7';

    public static String rgbCode(int rgb) {
        String hex = String.format(Locale.ROOT, "%06x", rgb & 0xFFFFFF);
        StringBuilder result = new StringBuilder(14).append(CODE).append('x');
        for (int i = 0; i < hex.length(); i++) {
            result.append(CODE).append(hex.charAt(i));
        }
        return result.toString();
    }

    public static boolean isRgbCodeAt(String text, int index) {
        if (index < 0 || index + 13 >= text.length()
                || text.charAt(index) != CODE
                || Character.toLowerCase(text.charAt(index + 1)) != 'x') {
            return false;
        }
        for (int digit = 0; digit < 6; digit++) {
            int codeIndex = index + 2 + digit * 2;
            if (text.charAt(codeIndex) != CODE || Character.digit(text.charAt(codeIndex + 1), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    public static int rgbAt(String text, int index) {
        if (!isRgbCodeAt(text, index)) {
            throw new IllegalArgumentException("No RGB formatting sequence at index " + index);
        }
        int rgb = 0;
        for (int digit = 0; digit < 6; digit++) {
            rgb = (rgb << 4) | Character.digit(text.charAt(index + 3 + digit * 2), 16);
        }
        return rgb;
    }

    public static String strip(String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int index = 0; index < text.length();) {
            if (text.charAt(index) == CODE && index + 1 < text.length()) {
                index += isRgbCodeAt(text, index) ? 14 : 2;
            } else {
                result.append(text.charAt(index++));
            }
        }
        return result.toString();
    }

    public static int rawIndex(String text, int plainIndex) {
        int index = 0;
        int visible = 0;
        while (index < text.length() && visible < plainIndex) {
            if (text.charAt(index) == CODE && index + 1 < text.length()) {
                index += isRgbCodeAt(text, index) ? 14 : 2;
            } else {
                index++;
                visible++;
            }
        }
        return index;
    }

    public static int leadingFormattingLength(String text) {
        int index = 0;
        while (index + 1 < text.length() && text.charAt(index) == CODE) {
            index += isRgbCodeAt(text, index) ? 14 : 2;
        }
        return index;
    }

    public static String preserveLastRgbColor(String text, String legacyFormatting) {
        int rgbIndex = -1;
        for (int index = 0; index + 1 < text.length();) {
            if (text.charAt(index) != CODE) {
                index++;
                continue;
            }
            if (isRgbCodeAt(text, index)) {
                rgbIndex = index;
                index += 14;
                continue;
            }
            char code = Character.toLowerCase(text.charAt(index + 1));
            if (isLegacyColor(code)) {
                rgbIndex = -1;
            }
            index += 2;
        }
        if (rgbIndex < 0) {
            return legacyFormatting;
        }

        String remaining = legacyFormatting;
        if (remaining.length() >= 2 && remaining.charAt(0) == CODE && isLegacyColor(remaining.charAt(1))) {
            remaining = remaining.substring(2);
        }
        return rgbCode(rgbAt(text, rgbIndex)) + remaining;
    }

    public static boolean isLegacyColor(char code) {
        char normalized = Character.toLowerCase(code);
        return normalized >= '0' && normalized <= '9'
                || normalized >= 'a' && normalized <= 'f'
                || normalized == 'g' || normalized == 'h';
    }
}
