package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public final class TutorialToast implements Toast {

    private static final ResourceLocation BACKGROUND = new ResourceLocation("ric", "textures/gui/sprites/toast/tutorial.png", false);
    private final Object token;
    private final TutorialToastIcon icon;
    private final TextComponent title;
    private final TextComponent message;
    private final boolean progressable;
    private ToastVisibility visibility = ToastVisibility.SHOW;
    private long lastProgressTime;
    private float lastProgress;
    private float progress;

    public TutorialToast(Object token, TutorialToastIcon icon, TextComponent title, @Nullable TextComponent message, boolean progressable) {
        this.token = token;
        this.icon = icon;
        this.title = title;
        this.message = message;
        this.progressable = progressable;
    }

    @Override
    public Object getToken() {
        return token;
    }

    @Override
    public ToastVisibility render(HudGraphics graphics, ToastManager manager, long visibleMillis) {
        graphics.blit(BACKGROUND, 0, 0, 0, 0, width(), height(), 160, 32);
        icon.render(graphics, 6, 6);
        graphics.drawString(title, 30, message == null ? 12 : 7, -11534256, false);
        if (message != null) graphics.drawString(message, 30, 18, 0xFF000000, false);
        if (progressable) {
            graphics.fill(3, 28, 157, 29, 0xFFFFFFFF);
            float fraction = Math.max(0, Math.min(1, (visibleMillis - lastProgressTime) / 100.0F));
            float displayed = lastProgress + fraction * (progress - lastProgress);
            graphics.fill(3, 28, (int) (3 + 154 * displayed), 29,
                    progress >= lastProgress ? 0xFF005500 : 0xFF550000);
            lastProgress = displayed;
            lastProgressTime = visibleMillis;
        }
        return visibility;
    }

    public void hide() {
        visibility = ToastVisibility.HIDE;
    }

    public void updateProgress(float progress) {
        this.progress = progress;
    }
}
