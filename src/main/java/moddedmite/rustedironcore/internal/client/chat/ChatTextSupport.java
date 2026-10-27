package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;

public final class ChatTextSupport {

    public static final char FORMAT_CODE = (char) 0x00A7;

    public static int rawIndex(String text, int plainIndex) {
        return LegacyTextFormatting.rawIndex(text, plainIndex);
    }

    public static int leadingFormattingLength(String text) {
        return LegacyTextFormatting.leadingFormattingLength(text);
    }
}
