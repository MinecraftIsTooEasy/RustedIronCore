package moddedmite.rustedironcore.internal.network.packets;

public final class CommandSuggestionRange {

    public static int startOfLastToken(String input) {
        return lastTokenStart(input, input.length());
    }

    public static int lastTokenStart(String input, int cursor) {
        int position = Math.min(cursor, input.length());
        while (position > 0 && !Character.isWhitespace(input.charAt(position - 1))) position--;
        return position;
    }

    public static int tokenEnd(String input, int cursor) {
        int position = Math.min(cursor, input.length());
        while (position < input.length() && !Character.isWhitespace(input.charAt(position))) position++;
        return position;
    }
}
