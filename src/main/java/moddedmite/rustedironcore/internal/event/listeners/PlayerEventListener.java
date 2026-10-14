package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.PlayerCrossDimensionEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerRespawnEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.api.util.FabricUtil;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.internal.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.internal.network.packets.S2CSyncNutritionLimit;
import moddedmite.rustedironcore.localization.internal.RICText;
import moddedmite.rustedironcore.network.Network;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class PlayerEventListener implements IPlayerEventListener {
    @Override
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        ServerPlayer player = event.player();
        if (this.shouldState(event.firstLogin())) {
            player.addChatMessage(RICText.STATEMENT.getKey());
            Network.sendToClient(player, new S2COpenGuiTips());
        }
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
    }

    private boolean shouldState(boolean firstLogin) {
        if (!firstLogin) return false;// if not first login, won't send
        if (FabricUtil.isDevelopmentEnvironment()) return false;
        if (RICConfig.StatementOnLogin.get()) return true;// if config is true, always send
        if (MinecraftServer.getServer().isServerInOnlineMode()) return false;// if online server, won't send
        return true;// send by default
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        ServerPlayer player = event.player();
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
    }

    @Override
    public void onPlayerCrossDimension(PlayerCrossDimensionEvent event) {
        ServerPlayer player = event.player();
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
    }
}
