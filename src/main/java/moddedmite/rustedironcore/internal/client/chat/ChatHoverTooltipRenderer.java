package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.FontRenderer;
import net.minecraft.Gui;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class ChatHoverTooltipRenderer extends Gui {

    private static final int BACKGROUND_COLOR = -267386864;
    private static final int BORDER_COLOR_TOP = 1347420415;
    private static final int BORDER_COLOR_BOTTOM = 1344798847;
    private static final ChatHoverTooltipRenderer INSTANCE = new ChatHoverTooltipRenderer();

    private ChatHoverTooltipRenderer() {}

    public static void render(FontRenderer fontRenderer, List<String> lines, int mouseX, int mouseY,
                              int screenWidth, int screenHeight) {
        if (lines.isEmpty()) {
            return;
        }

        int width = 0;
        for (String line : lines) {
            width = Math.max(width, fontRenderer.getStringWidth(line));
        }
        int height = lines.size() * 10;
        int x = mouseX + 12;
        int y = mouseY - 12;
        if (x + width > screenWidth) {
            x = Math.max(x - 24 - width, 4);
        }
        if (y + height + 3 > screenHeight) {
            y = screenHeight - height - 3;
        }

        INSTANCE.zLevel = 400.0F;
        INSTANCE.drawTooltipBackground(x, y, width, height);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 400.0F);
        for (int index = 0; index < lines.size(); index++) {
            fontRenderer.drawStringWithShadow(lines.get(index), x, y + index * 10, 0xFFFFFFFF);
        }
        GL11.glPopMatrix();
        INSTANCE.zLevel = 0.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void drawTooltipBackground(int x, int y, int width, int height) {
        int left = x - 3;
        int top = y - 3;
        int totalWidth = width + 6;
        int totalHeight = height + 6;
        this.drawTooltipHorizontalLine(left, top - 1, totalWidth, BACKGROUND_COLOR);
        this.drawTooltipHorizontalLine(left, top + totalHeight, totalWidth, BACKGROUND_COLOR);
        this.drawGradientRect(left, top, left + totalWidth, top + totalHeight, BACKGROUND_COLOR, BACKGROUND_COLOR);
        this.drawGradientRect(left - 1, top, left, top + totalHeight, BACKGROUND_COLOR, BACKGROUND_COLOR);
        this.drawGradientRect(left + totalWidth, top, left + totalWidth + 1, top + totalHeight,
                BACKGROUND_COLOR, BACKGROUND_COLOR);

        this.drawGradientRect(left, top + 1, left + 1, top + totalHeight - 1,
                BORDER_COLOR_TOP, BORDER_COLOR_BOTTOM);
        this.drawGradientRect(left + totalWidth - 1, top + 1, left + totalWidth, top + totalHeight - 1,
                BORDER_COLOR_TOP, BORDER_COLOR_BOTTOM);
        this.drawTooltipHorizontalLine(left, top, totalWidth, BORDER_COLOR_TOP);
        this.drawTooltipHorizontalLine(left, top + totalHeight - 1, totalWidth, BORDER_COLOR_BOTTOM);
    }

    private void drawTooltipHorizontalLine(int x, int y, int width, int color) {
        this.drawGradientRect(x, y, x + width, y + 1, color, color);
    }
}
