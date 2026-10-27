package moddedmite.rustedironcore.internal.client.hud;

import moddedmite.rustedironcore.client.api.hud.HudGraphics;
import moddedmite.rustedironcore.client.api.hud.Toast;
import moddedmite.rustedironcore.client.api.hud.ToastManager;
import moddedmite.rustedironcore.client.api.hud.ToastVisibility;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ToastInstance {

    private final Toast toast;
    private final int index;
    private final int slots;
    private long animationTime = -1;
    private long visibleTime = -1;
    private ToastVisibility visibility = ToastVisibility.SHOW;
    private boolean forcedHide;

    public ToastInstance(Toast toast, int index, int slots) {
        this.toast = toast;
        this.index = index;
        this.slots = slots;
    }

    public Toast getToast() {
        return toast;
    }

    public int getIndex() {
        return index;
    }

    public int getSlots() {
        return slots;
    }

    public void hide() {
        forcedHide = true;
    }

    private float getVisibility(long now) {
        float fraction = Math.max(0, Math.min(1, (now - animationTime) / 600.0F));
        fraction *= fraction;
        return visibility == ToastVisibility.HIDE ? 1 - fraction : fraction;
    }

    public boolean render(HudGraphics graphics, ToastManager manager) {
        long now = System.nanoTime() / 1_000_000L;
        if (animationTime == -1) {
            animationTime = now;
            visibility.playSound();
        }
        if (visibility == ToastVisibility.SHOW && now - animationTime <= 600) visibleTime = now;

        graphics.pushPose();
        ToastVisibility next;
        try {
            graphics.translate(graphics.guiWidth() - toast.width() * getVisibility(now), index * 32, 800);
            next = toast.render(graphics, manager, now - visibleTime);
            if (forcedHide) next = ToastVisibility.HIDE;
        } finally {
            graphics.popPose();
        }
        if (next != visibility) {
            // Preserve the visible position when reversing the vanilla 600-ms squared animation.
            animationTime = now - (int) ((1 - getVisibility(now)) * 600);
            visibility = next;
            visibility.playSound();
        }
        return visibility == ToastVisibility.HIDE && now - animationTime > 600;
    }
}
