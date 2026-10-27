package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;

import java.util.UUID;

public abstract class BossBar {

    private final UUID id;
    protected TextComponent name;
    protected float progress = 1.0F;
    protected BossBarColor color;
    protected BossBarOverlay overlay;
    protected boolean darkenScreen;
    protected boolean playBossMusic;
    protected boolean createWorldFog;

    protected BossBar(UUID id, TextComponent name, BossBarColor color, BossBarOverlay overlay) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.overlay = overlay;
    }

    public UUID getId() {
        return id;
    }

    public TextComponent getName() {
        return name;
    }

    public void setName(TextComponent name) {
        this.name = name;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public BossBarColor getColor() {
        return color;
    }

    public void setColor(BossBarColor color) {
        this.color = color;
    }

    public BossBarOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(BossBarOverlay overlay) {
        this.overlay = overlay;
    }

    public boolean shouldDarkenScreen() {
        return darkenScreen;
    }

    public BossBar setDarkenScreen(boolean darkenScreen) {
        this.darkenScreen = darkenScreen;
        return this;
    }

    public boolean shouldPlayBossMusic() {
        return playBossMusic;
    }

    public BossBar setPlayBossMusic(boolean playBossMusic) {
        this.playBossMusic = playBossMusic;
        return this;
    }

    public boolean shouldCreateWorldFog() {
        return createWorldFog;
    }

    public BossBar setCreateWorldFog(boolean createWorldFog) {
        this.createWorldFog = createWorldFog;
        return this;
    }
}
