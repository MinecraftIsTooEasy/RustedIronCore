package moddedmite.rustedironcore.internal.client.chat;

public final class ChatInputTextFormatter {

    public static String format(ChatCommandSuggestionsAccess suggestions, String text, int textStart, int defaultColor) {
        StringBuilder formattedText = new StringBuilder(text.length() + 8);
        int activeColor = defaultColor;
        for (int index = 0; index < text.length(); index++) {
            int nextColor = suggestions.ric$getInputTextColor(textStart + index, defaultColor);
            if (nextColor != activeColor) {
                formattedText.append('\u00a7').append(formattingCode(nextColor, defaultColor));
                activeColor = nextColor;
            }
            formattedText.append(text.charAt(index));
        }
        return formattedText.toString();
    }

    private static char formattingCode(int color, int defaultColor) {
        int rgb = color & 0xFFFFFF;
        if (rgb == (defaultColor & 0xFFFFFF)) return 'r';
        return switch (rgb) {
            case 0xAAAAAA -> '7';
            case 0xFF5555 -> 'c';
            case 0x55FFFF -> 'b';
            case 0xFFFF55 -> 'e';
            case 0x55FF55 -> 'a';
            case 0xFF55FF -> 'd';
            case 0xFFAA00 -> '6';
            default -> 'r';
        };
    }
}
