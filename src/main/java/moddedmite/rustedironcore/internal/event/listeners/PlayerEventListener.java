package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

public class PlayerEventListener implements IPlayerEventListener {
    @Override
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        ServerPlayer player = event.player();
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
        player.addChatMessage("ric.statement");
        if (this.shouldSendPacket(event.firstLogin())) {
            Network.sendToClient(player, new S2COpenGuiTips());
        }
    }

    private boolean shouldSendPacket(boolean firstLogin) {
        if (!firstLogin) return false;// if not first login, won't send
        if (RICConfig.StatementOnLogin.get()) return true;// if config is true, always send
        if (MinecraftServer.getServer().isServerInOnlineMode()) return false;// if online server, won't send
        return true;// send by default
    }
}
