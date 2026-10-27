package moddedmite.rustedironcore.internal.client.chat;

import com.google.gson.JsonElement;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.api.chat.TextHoverEvent;
import moddedmite.rustedironcore.internal.chat.LegacyTextFormatting;
import net.minecraft.FontRenderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class ChatInteractionRenderer {

    public static void renderClickHighlight(FontRenderer fontRenderer, String lineText, ChatInteractionSpan span,
                                            int displayLine, float chatScale) {
        int rawStart = LegacyTextFormatting.rawIndex(lineText, span.start());
        int rawEnd = LegacyTextFormatting.rawIndex(lineText, span.end());
        int x = fontRenderer.getStringWidth(lineText.substring(0, rawStart));
        String spanText = lineText.substring(rawStart, rawEnd);
        int leadingFormattingLength = LegacyTextFormatting.leadingFormattingLength(spanText);
        String highlighted = spanText.substring(0, leadingFormattingLength)
                + LegacyTextFormatting.CODE + "n" + spanText.substring(leadingFormattingLength);

        GL11.glPushMatrix();
        GL11.glTranslatef(2.0F, 20.0F, 0.0F);
        GL11.glScalef(chatScale, chatScale, 1.0F);
        fontRenderer.drawStringWithShadow(highlighted, x, -displayLine * 9 - 26, 0xFFFFFFFF);
        GL11.glPopMatrix();
    }

    public static void renderHoverTooltip(FontRenderer fontRenderer, TextHoverEvent hoverEvent,
                                          int mouseX, int mouseY, int screenWidth, int screenHeight) {
        if (hoverEvent == null || hoverEvent.action() != TextHoverEvent.Action.SHOW_TEXT) return;
        try {
            JsonElement contents = hoverEvent.contents();
            TextComponent tooltip = TextComponentSerializer.fromJson(contents);
            List<String> lines = new ArrayList<>();
            for (Object line : fontRenderer.listFormattedStringToWidth(MiteTextComponentAdapter.toFormattedString(tooltip), 200)) {
                lines.add((String) line);
            }
            ChatHoverTooltipRenderer.render(fontRenderer, lines, mouseX, mouseY, screenWidth, screenHeight);
        } catch (RuntimeException ignored) {
            // Invalid hover payloads do not affect chat rendering.
        }
    }
}
