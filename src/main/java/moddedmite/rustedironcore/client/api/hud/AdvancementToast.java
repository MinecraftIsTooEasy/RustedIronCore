package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.sound.ResourceSoundPlayer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.List;

/** Vanilla advancement presentation, independent of a mod's advancement/achievement implementation. */
@Environment(EnvType.CLIENT)
public final class AdvancementToast implements Toast {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("ric", "textures/gui/sprites/toast/advancement.png", false);
    private static final ResourceLocation CHALLENGE_SOUND = new ResourceLocation("ric", "sounds/ui/toast/challenge_complete.ogg", false);
    private final Object token;
    private final TextComponent title;
    private final ItemStack icon;
    private final AdvancementToastFrame frame;
    private boolean playedSound;

    public AdvancementToast(Object token, TextComponent title, ItemStack icon, AdvancementToastFrame frame) {
        this.token = token;
        this.title = title;
        this.icon = icon.copy();
        this.frame = frame;
    }

    @Override
    public Object getToken() {
        return token;
    }

    @Override
    public ToastVisibility render(HudGraphics graphics, ToastManager manager, long visibleMillis) {
        graphics.blit(BACKGROUND, 0, 0, 0, 0, width(), height(), 160, 32);
        List<String> lines = graphics.split(title, 125);
        int frameColor = frame == AdvancementToastFrame.CHALLENGE ? 16746751 : 16776960;
        if (lines.size() == 1) {
            graphics.drawString(frame.getDisplayName(), 30, 7, frameColor | 0xFF000000, false);
            graphics.drawString(lines.get(0), 30, 18, 0xFFFFFFFF, false);
        } else if (visibleMillis < 1500) {
            int alpha = (int) (Math.max(0, Math.min(1, (1500 - visibleMillis) / 300.0F)) * 255);
            graphics.drawString(frame.getDisplayName(), 30, 11, frameColor | alpha << 24 | 0x04000000, false);
        } else {
            int alpha = (int) (Math.max(0, Math.min(1, (visibleMillis - 1500) / 300.0F)) * 252);
            int y = height() / 2 - lines.size() * 9 / 2;
            for (String line : lines) {
                graphics.drawString(line, 30, y, 0xFFFFFF | alpha << 24 | 0x04000000, false);
                y += 9;
            }
        }
        if (!playedSound && visibleMillis > 0) {
            playedSound = true;
            if (frame == AdvancementToastFrame.CHALLENGE) ResourceSoundPlayer.play(CHALLENGE_SOUND, 1, 1);
        }
        graphics.renderItem(icon, 8, 8);
        return visibleMillis >= 5000 * manager.getNotificationDisplayTimeMultiplier()
                ? ToastVisibility.HIDE : ToastVisibility.SHOW;
    }
}
