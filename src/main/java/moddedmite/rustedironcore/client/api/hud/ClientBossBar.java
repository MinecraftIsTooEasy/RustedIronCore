package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.hud.BossBar;
import moddedmite.rustedironcore.api.hud.BossBarColor;
import moddedmite.rustedironcore.api.hud.BossBarOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class ClientBossBar extends BossBar {

    private float targetProgress;
    private long lastUpdateMillis;

    public ClientBossBar(UUID id, TextComponent name, float progress, BossBarColor color, BossBarOverlay overlay) {
        super(id, name, color, overlay);
        this.progress = progress;
        targetProgress = progress;
        lastUpdateMillis = now();
    }

    @Override
    public void setProgress(float progress) {
        this.progress = getProgress();
        targetProgress = progress;
        lastUpdateMillis = now();
    }

    @Override
    public float getProgress() {
        float fraction = Math.max(0, Math.min(1, (now() - lastUpdateMillis) / 100.0F));
        return progress + fraction * (targetProgress - progress);
    }

    private static long now() {
        return System.nanoTime() / 1_000_000L;
    }
}
