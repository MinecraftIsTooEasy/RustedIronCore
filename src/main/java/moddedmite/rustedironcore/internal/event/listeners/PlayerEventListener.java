package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
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
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
        if (this.shouldState(event.firstLogin())) {
            player.addChatMessage(RICText.Statement.getKey());
            Network.sendToClient(player, new S2COpenGuiTips());
        }
    }

    private boolean shouldState(boolean firstLogin) {
        if (!firstLogin) return false;// if not first login, won't send
        if (FabricUtil.isDevelopmentEnvironment()) return false;
        if (RICConfig.StatementOnLogin.get()) return true;// if config is true, always send
        if (MinecraftServer.getServer().isServerInOnlineMode()) return false;// if online server, won't send
        return true;// send by default
    }
}
