package moddedmite.rustedironcore.client.api.hud;

import moddedmite.rustedironcore.api.hud.BossBarOverlay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public final class BossOverlay {

    private static final ResourceLocation[] BAR_BACKGROUNDS = {
            texture("pink_background"), texture("blue_background"), texture("red_background"),
            texture("green_background"), texture("yellow_background"), texture("purple_background"), texture("white_background")
    };
    private static final ResourceLocation[] BAR_PROGRESS = {
            texture("pink_progress"), texture("blue_progress"), texture("red_progress"),
            texture("green_progress"), texture("yellow_progress"), texture("purple_progress"), texture("white_progress")
    };
    private static final ResourceLocation[] OVERLAY_BACKGROUNDS = {
            texture("notched_6_background"), texture("notched_10_background"),
            texture("notched_12_background"), texture("notched_20_background")
    };
    private static final ResourceLocation[] OVERLAY_PROGRESS = {
            texture("notched_6_progress"), texture("notched_10_progress"),
            texture("notched_12_progress"), texture("notched_20_progress")
    };
    private final Map<UUID, ClientBossBar> bars = new LinkedHashMap<>();

    private static ResourceLocation texture(String name) {
        return new ResourceLocation("ric", "textures/gui/sprites/boss_bar/" + name + ".png", false);
    }

    public void add(ClientBossBar bar) {
        bars.put(bar.getId(), bar);
    }

    public ClientBossBar get(UUID id) {
        return bars.get(id);
    }

    public void remove(UUID id) {
        bars.remove(id);
    }

    public Map<UUID, ClientBossBar> getBars() {
        return Collections.unmodifiableMap(bars);
    }

    public void clear() {
        bars.clear();
    }

    public boolean shouldDarkenScreen() {
        for (ClientBossBar bar : bars.values()) if (bar.shouldDarkenScreen()) return true;
        return false;
    }

    public boolean shouldPlayMusic() {
        for (ClientBossBar bar : bars.values()) if (bar.shouldPlayBossMusic()) return true;
        return false;
    }

    public boolean shouldCreateWorldFog() {
        for (ClientBossBar bar : bars.values()) if (bar.shouldCreateWorldFog()) return true;
        return false;
    }

    public void render(HudGraphics graphics, boolean legacyBossVisible) {
        if (bars.isEmpty()) return;
        int x = graphics.guiWidth() / 2 - 91;
        int y = legacyBossVisible ? 31 : 12;
        graphics.begin();
        try {
            for (ClientBossBar bar : bars.values()) {
                drawBar(graphics, bar, x, y, 182, BAR_BACKGROUNDS, OVERLAY_BACKGROUNDS);
                // 1.21.1 Mth.lerpDiscrete(progress, 0, 182): positive progress includes its leading pixel.
                float progress = bar.getProgress();
                int progressWidth = (int) Math.floor(progress * 181) + (progress > 0 ? 1 : 0);
                if (progressWidth > 0) drawBar(graphics, bar, x, y, progressWidth, BAR_PROGRESS, OVERLAY_PROGRESS);
                graphics.drawCenteredString(bar.getName(), graphics.guiWidth() / 2, y - 9, 0xFFFFFF);
                y += 10 + graphics.getFont().FONT_HEIGHT;
                if (y >= graphics.guiHeight() / 3) break;
            }
        } finally {
            graphics.end();
        }
    }

    private static void drawBar(HudGraphics graphics, ClientBossBar bar, int x, int y, int width,
                                ResourceLocation[] backgrounds, ResourceLocation[] overlays) {
        graphics.blit(backgrounds[bar.getColor().ordinal()], x, y, 0, 0, width, 5, 182, 5);
        if (bar.getOverlay() != BossBarOverlay.PROGRESS) {
            graphics.blit(overlays[bar.getOverlay().ordinal() - 1], x, y, 0, 0, width, 5, 182, 5);
        }
    }
}
