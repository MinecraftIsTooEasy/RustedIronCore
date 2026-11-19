package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.Minecraft;

/** Colour of the {@code §x} RGB code currently active in {@code FontRenderer.renderStringAtPos}. */
public interface RgbTextColor {
    /** The active RGB colour, -1 when a legacy colour (or none) is active. */
    int ric$activeRgbColor();

    /**
     * {@code rgb} as the RGBA colour {@code renderStringAtPos} sets for a legacy colour code: anaglyph-adjusted and, for the shadow
     * pass, darkened the way MITE darkens its own codes.
     */
    static float[] toTextColor(int rgb, boolean shadow, float alpha) {
        int r = rgb >> 16 & 255;
        int g = rgb >> 8 & 255;
        int b = rgb & 255;
        if (Minecraft.theMinecraft.gameSettings.anaglyph) {
            int luminance = (r * 30 + g * 59 + b * 11) / 100;
            g = (r * 30 + g * 70) / 100;
            b = (r * 30 + b * 70) / 100;
            r = luminance;
        }
        if (shadow) {
            r /= 4;
            g /= 4;
            b /= 4;
        }
        return new float[]{r / 255.0F, g / 255.0F, b / 255.0F, alpha};
    }
}
