package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import net.minecraft.FontRenderer;

public final class LegacyTextWidth {

    public static int sizeStringToWidth(FontRenderer fontRenderer, String text, int maxWidth) {
        int length = text.length();
        int width = 0;
        int index = 0;
        int lastSpace = -1;
        boolean bold = false;

        for (; index < length; index++) {
            char character = text.charAt(index);
            if (character == '\n') {
                lastSpace = index;
                break;
            }
            if (character == LegacyTextFormatting.CODE) {
                if (LegacyTextFormatting.isRgbCodeAt(text, index)) {
                    index += 13;
                    bold = false;
                    continue;
                }
                if (index < length - 1) {
                    char formatting = text.charAt(++index);
                    if (formatting == 'l' || formatting == 'L') {
                        bold = true;
                    } else if (formatting == 'r' || formatting == 'R' || LegacyTextFormatting.isLegacyColor(formatting)) {
                        bold = false;
                    }
                }
                continue;
            }
            if (character == ' ') {
                lastSpace = index;
            }
            width += fontRenderer.getCharWidth(character);
            if (bold) {
                width++;
            }
            if (width > maxWidth) {
                break;
            }
        }

        return index != length && lastSpace != -1 && lastSpace < index ? lastSpace : index;
    }
}
