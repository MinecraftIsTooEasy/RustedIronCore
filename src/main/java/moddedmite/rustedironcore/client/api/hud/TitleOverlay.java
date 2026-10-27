package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class TitleOverlay {

    private TextComponent title;
    private TextComponent subtitle;
    private int remainingTicks;
    private int fadeInTicks = 10;
    private int stayTicks = 70;
    private int fadeOutTicks = 20;

    public void setTitle(TextComponent title) {
        this.title = title;
        remainingTicks = fadeInTicks + stayTicks + fadeOutTicks;
    }

    public void setSubtitle(TextComponent subtitle) {
        this.subtitle = subtitle;
    }

    public void setTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
        if (fadeInTicks >= 0) this.fadeInTicks = fadeInTicks;
        if (stayTicks >= 0) this.stayTicks = stayTicks;
        if (fadeOutTicks >= 0) this.fadeOutTicks = fadeOutTicks;
        if (remainingTicks > 0) remainingTicks = this.fadeInTicks + this.stayTicks + this.fadeOutTicks;
    }

    public void clear() {
        title = null;
        subtitle = null;
        remainingTicks = 0;
    }

    public void resetTimes() {
        fadeInTicks = 10;
        stayTicks = 70;
        fadeOutTicks = 20;
    }

    public void tick() {
        if (remainingTicks > 0 && --remainingTicks <= 0) clear();
    }

    public void render(HudGraphics graphics, float partialTick) {
        if (title == null || remainingTicks <= 0) return;
        // 1.21.1 Gui.renderTitle: title scale 4, subtitle scale 2; origin is the integer GUI center.
        float remaining = remainingTicks - partialTick;
        int alpha = 255;
        if (remainingTicks > fadeOutTicks + stayTicks) {
            alpha = (int) ((fadeInTicks + stayTicks + fadeOutTicks - remaining) * 255.0F / fadeInTicks);
        }
        if (remainingTicks <= fadeOutTicks) alpha = (int) (remaining * 255.0F / fadeOutTicks);
        alpha = Math.max(0, Math.min(255, alpha));
        if (alpha <= 8) return;

        graphics.begin();
        try {
            graphics.translate(graphics.guiWidth() / 2, graphics.guiHeight() / 2, 0);
            graphics.pushPose();
            graphics.scale(4, 4, 4);
            graphics.drawStringWithBackdrop(title, -graphics.width(title) / 2, -10, 0xFFFFFF | alpha << 24);
            graphics.popPose();
            if (subtitle != null) {
                graphics.pushPose();
                graphics.scale(2, 2, 2);
                graphics.drawStringWithBackdrop(subtitle, -graphics.width(subtitle) / 2, 5, 0xFFFFFF | alpha << 24);
                graphics.popPose();
            }
        } finally {
            graphics.end();
        }
    }
}
