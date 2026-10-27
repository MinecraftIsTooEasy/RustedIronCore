package moddedmite.rustedironcore.api.hud;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.internal.hud.HudOperation;
import net.minecraft.ServerPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** Vanilla ServerBossEvent membership, visibility and incremental update semantics. */
public final class ServerBossBar extends BossBar {

    private final HudChannel channel;
    private final Set<ServerPlayer> players = new HashSet<>();
    private final Set<ServerPlayer> playerView = Collections.unmodifiableSet(players);
    private boolean visible = true;

    public ServerBossBar(HudChannel channel, TextComponent name, BossBarColor color, BossBarOverlay overlay) {
        super(UUID.randomUUID(), name, color, overlay);
        this.channel = channel;
    }

    @Override
    public void setProgress(float progress) {
        if (progress != this.progress) {
            super.setProgress(progress);
            broadcast(HudOperation.BOSS_PROGRESS);
        }
    }

    @Override
    public void setName(TextComponent name) {
        if (!name.equals(this.name)) {
            super.setName(name);
            broadcast(HudOperation.BOSS_NAME);
        }
    }

    @Override
    public void setColor(BossBarColor color) {
        if (color != this.color) {
            super.setColor(color);
            broadcast(HudOperation.BOSS_STYLE);
        }
    }

    @Override
    public void setOverlay(BossBarOverlay overlay) {
        if (overlay != this.overlay) {
            super.setOverlay(overlay);
            broadcast(HudOperation.BOSS_STYLE);
        }
    }

    @Override
    public ServerBossBar setDarkenScreen(boolean value) {
        if (value != darkenScreen) {
            super.setDarkenScreen(value);
            broadcast(HudOperation.BOSS_PROPERTIES);
        }
        return this;
    }

    @Override
    public ServerBossBar setPlayBossMusic(boolean value) {
        if (value != playBossMusic) {
            super.setPlayBossMusic(value);
            broadcast(HudOperation.BOSS_PROPERTIES);
        }
        return this;
    }

    @Override
    public ServerBossBar setCreateWorldFog(boolean value) {
        if (value != createWorldFog) {
            super.setCreateWorldFog(value);
            broadcast(HudOperation.BOSS_PROPERTIES);
        }
        return this;
    }

    public void addPlayer(ServerPlayer player) {
        if (players.add(player) && visible) channel.sendBossBar(player, this, HudOperation.BOSS_ADD);
    }

    public void removePlayer(ServerPlayer player) {
        if (players.remove(player) && visible) channel.removeBossBar(player, getId());
    }

    public void removeAllPlayers() {
        if (visible) {
            for (ServerPlayer player : players) channel.removeBossBar(player, getId());
        }
        players.clear();
    }

    public Collection<ServerPlayer> getPlayers() {
        return playerView;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) return;
        this.visible = visible;
        for (ServerPlayer player : players) {
            if (visible) channel.sendBossBar(player, this, HudOperation.BOSS_ADD);
            else channel.removeBossBar(player, getId());
        }
    }

    private void broadcast(HudOperation operation) {
        if (visible) {
            for (ServerPlayer player : players) channel.sendBossBar(player, this, operation);
        }
    }
}
