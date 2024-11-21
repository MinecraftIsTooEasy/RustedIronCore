package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import net.minecraft.ServerPlayer;

public class PlayerEventListener implements IPlayerEventListener {
    @Override
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        ServerPlayer player = event.player();
        Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).ric$GetNutritionLimit()));
        player.addChatMessage("ric.statement");
        if (event.firstLogin()) {
            Network.sendToClient(player, new S2COpenGuiTips());
        }
    }
}
