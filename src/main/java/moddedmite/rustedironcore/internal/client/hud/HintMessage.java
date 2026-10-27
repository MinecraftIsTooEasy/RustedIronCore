package moddedmite.rustedironcore.internal.client.hud;

import moddedmite.rustedironcore.api.hud.HudChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record HintMessage(HudChannel channel, String text, int shownTick, int repeatDelayTicks) {

    // GuiIngame's native Action Bar lasts 60 ticks and fades over its final 20 ticks.
    public static final int DISPLAY_TICKS = 60;
    public static final int FADE_TICKS = 20;

    public boolean shouldSuppress(HudChannel channel, String text, int tick, int repeatDelayTicks) {
        return this.channel == channel && this.text.equals(text) && tick - shownTick < repeatDelayTicks;
    }

    public boolean hasExpired(int tick) {
        return tick - shownTick >= Math.max(DISPLAY_TICKS, repeatDelayTicks);
    }

    public int alpha(int tick, float partialTick) {
        float remaining = DISPLAY_TICKS - (tick - shownTick) - partialTick;
        return Math.max(0, Math.min(255, (int) (remaining * 255.0F / FADE_TICKS)));
    }
}
