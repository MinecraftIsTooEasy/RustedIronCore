package moddedmite.rustedironcore.internal.client.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class HudColors {

    /** Minecraft 1.21.1 Mth.hsvToArgb channel calculation, without the caller-supplied alpha. */
    public static int hsvToRgb(float hue, float saturation, float value) {
        int sector = (int) (hue * 6.0F) % 6;
        float fraction = hue * 6.0F - sector;
        float minimum = value * (1.0F - saturation);
        float falling = value * (1.0F - fraction * saturation);
        float rising = value * (1.0F - (1.0F - fraction) * saturation);
        float red;
        float green;
        float blue;
        switch (sector) {
            case 0 -> {
                red = value;
                green = rising;
                blue = minimum;
            }
            case 1 -> {
                red = falling;
                green = value;
                blue = minimum;
            }
            case 2 -> {
                red = minimum;
                green = value;
                blue = rising;
            }
            case 3 -> {
                red = minimum;
                green = falling;
                blue = value;
            }
            case 4 -> {
                red = rising;
                green = minimum;
                blue = value;
            }
            case 5 -> {
                red = value;
                green = minimum;
                blue = falling;
            }
            default -> throw new IllegalArgumentException("Invalid HSV color: " + hue + ", " + saturation + ", " + value);
        }
        return channel(red) << 16 | channel(green) << 8 | channel(blue);
    }

    private static int channel(float value) {
        return Math.max(0, Math.min(255, (int) (value * 255.0F)));
    }
}
