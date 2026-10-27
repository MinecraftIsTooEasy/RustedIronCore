package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.client.chat.MiteTextComponentAdapter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.FontRenderer;
import net.minecraft.Gui;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.RenderHelper;
import net.minecraft.RenderItem;
import net.minecraft.ResourceLocation;
import net.minecraft.ScaledResolution;
import net.minecraft.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/** Small GuiGraphics bridge for custom modern-style HUD and toast renderers. Coordinates use GUI pixels. */
@Environment(EnvType.CLIENT)
public final class HudGraphics extends Gui {

    private final Minecraft minecraft;
    private final RenderItem itemRenderer = new RenderItem();
    private float textBackgroundOpacity;

    public HudGraphics(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    public Minecraft getMinecraft() {
        return minecraft;
    }

    public FontRenderer getFont() {
        return minecraft.fontRenderer;
    }

    public int guiWidth() {
        return new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight).getScaledWidth();
    }

    public int guiHeight() {
        return new ScaledResolution(minecraft.gameSettings, minecraft.displayWidth, minecraft.displayHeight).getScaledHeight();
    }

    public String format(TextComponent text) {
        return MiteTextComponentAdapter.toFormattedString(text);
    }

    public int width(TextComponent text) {
        return getFont().getStringWidth(format(text));
    }

    public List<String> split(TextComponent text, int width) {
        List<String> lines = new ArrayList<>();
        for (Object line : getFont().listFormattedStringToWidth(format(text), Math.max(1, width))) {
            lines.add((String) line);
        }
        return lines;
    }

    public void drawString(TextComponent text, int x, int y, int color, boolean shadow) {
        drawString(format(text), x, y, color, shadow);
    }

    public void drawString(String text, int x, int y, int color, boolean shadow) {
        getFont().drawString(text, x, y, color, shadow);
    }

    public void drawCenteredString(TextComponent text, int centerX, int y, int color) {
        drawString(text, centerX - width(text) / 2, y, color, true);
    }

    public float getTextBackgroundOpacity() {
        return textBackgroundOpacity;
    }

    /** Equivalent to the 1.21.1 background-for-all-text opacity; zero keeps the vanilla chat-only default. */
    public void setTextBackgroundOpacity(float opacity) {
        if (!Float.isFinite(opacity) || opacity < 0 || opacity > 1) {
            throw new IllegalArgumentException("Text background opacity must be between 0 and 1");
        }
        textBackgroundOpacity = opacity;
    }

    public int drawStringWithBackdrop(TextComponent text, int x, int y, int color) {
        return drawStringWithBackdrop(format(text), x, y, color);
    }

    /** 1.21.1 GuiGraphics.drawStringWithBackdrop: two-pixel padding, fading background and text shadow. */
    public int drawStringWithBackdrop(String text, int x, int y, int color) {
        int alpha = (int) (textBackgroundOpacity * 255) * (color >>> 24) / 255;
        if (alpha > 0) {
            fill(x - 2, y - 2, x + getFont().getStringWidth(text) + 2, y + 11, alpha << 24);
            // Legacy Gui.drawRect disables blending after drawing; modern GuiGraphics keeps it available.
            GL11.glEnable(GL11.GL_BLEND);
        }
        return getFont().drawString(text, x, y, color, true);
    }

    public void fill(int left, int top, int right, int bottom, int color) {
        drawRect(left, top, right, bottom, color);
    }

    public void blit(ResourceLocation texture, int x, int y, int u, int v, int width, int height) {
        blit(texture, x, y, u, v, width, height, 256, 256);
    }

    public void blit(ResourceLocation texture, int x, int y, int u, int v, int width, int height,
                     int textureWidth, int textureHeight) {
        minecraft.getTextureManager().bindTexture(texture);
        GL11.glColor4f(1, 1, 1, 1);
        Tessellator vertices = Tessellator.instance;
        vertices.startDrawingQuads();
        vertices.addVertexWithUV(x, y + height, zLevel, (double) u / textureWidth, (double) (v + height) / textureHeight);
        vertices.addVertexWithUV(x + width, y + height, zLevel, (double) (u + width) / textureWidth, (double) (v + height) / textureHeight);
        vertices.addVertexWithUV(x + width, y, zLevel, (double) (u + width) / textureWidth, (double) v / textureHeight);
        vertices.addVertexWithUV(x, y, zLevel, (double) u / textureWidth, (double) v / textureHeight);
        vertices.draw();
    }

    public void renderItem(ItemStack item, int x, int y) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRenderer.renderItemAndEffectIntoGUI(getFont(), minecraft.getTextureManager(), item, x, y);
        } finally {
            GL11.glPopAttrib();
        }
    }

    public void pushPose() {
        GL11.glPushMatrix();
    }

    public void popPose() {
        GL11.glPopMatrix();
    }

    public void translate(float x, float y, float z) {
        GL11.glTranslatef(x, y, z);
    }

    public void scale(float x, float y, float z) {
        GL11.glScalef(x, y, z);
    }

    public void begin() {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_LIGHTING_BIT);
        pushPose();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public void end() {
        popPose();
        GL11.glPopAttrib();
    }
}
