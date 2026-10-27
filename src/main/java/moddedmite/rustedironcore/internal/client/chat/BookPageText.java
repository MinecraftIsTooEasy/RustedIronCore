package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.chat.TextStyle;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import net.minecraft.FontRenderer;
import net.minecraft.NBTTagList;
import net.minecraft.NBTTagString;

import java.util.List;

public final class BookPageText {

    public static TextComponent readPage(NBTTagList pages, int pageIndex) {
        if (pages == null || pageIndex < 0 || pageIndex >= pages.tagCount()) return null;
        String page = ((NBTTagString) pages.tagAt(pageIndex)).data;
        try {
            return TextComponentSerializer.fromJson(page);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static TextStyle styleAt(TextComponent component, FontRenderer fontRenderer, int screenWidth,
                                    int bookImageWidth, int mouseX, int mouseY) {
        if (component == null) return null;
        int originX = (screenWidth - bookImageWidth) / 2 + 36;
        int originY = 42;
        int line = (mouseY - originY) / fontRenderer.FONT_HEIGHT;
        if (line < 0 || mouseY < originY || line >= 14) return null;

        String formatted;
        try {
            formatted = MiteTextComponentAdapter.toMiteComponent(component).toStringWithFormatting(true);
        } catch (UnsupportedOperationException exception) {
            return null;
        }
        List<?> lines = fontRenderer.listFormattedStringToWidth(formatted, 116);
        if (line >= lines.size()) return null;
        String lineText = (String) lines.get(line);
        int relativeX = mouseX - originX;
        if (relativeX < 0) return null;
        String prefix = fontRenderer.trimStringToWidth(lineText, relativeX);
        int globalCharacter = LegacyTextFormatting.strip(prefix).length();
        for (int previousLine = 0; previousLine < line; previousLine++) {
            globalCharacter += LegacyTextFormatting.strip((String) lines.get(previousLine)).length();
        }
        return findStyle(component, globalCharacter, TextStyle.EMPTY);
    }

    private static TextStyle findStyle(TextComponent component, int target, TextStyle inherited) {
        TextStyle effective = component.getStyle().applyTo(inherited);
        int contentLength = LegacyTextFormatting.strip(MiteTextComponentAdapter.toFormattedString(
                new TextComponent(component.getContent()).setStyle(component.getStyle()))).length();
        if (target < contentLength) return effective;
        int offset = contentLength;
        for (TextComponent sibling : component.getSiblings()) {
            int length = LegacyTextFormatting.strip(MiteTextComponentAdapter.toFormattedString(sibling)).length();
            if (target < offset + length) return findStyle(sibling, target - offset, effective);
            offset += length;
        }
        return null;
    }
}
