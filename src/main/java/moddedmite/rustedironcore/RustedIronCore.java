package moddedmite.rustedironcore;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.player.ServerPlayerAPI;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.minecraft.ServerPlayer;
import net.xiaoyu233.fml.FishModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_ID = "RustedIronCore";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        if (!FishModLoader.isServer()) {
            this.initClient();
        }
        this.initServer();
        this.registerVanillaEvents();
    }

    private void initClient() {
        Network.initClient(packet -> Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet.toVanilla()));
    }

    private void initServer() {
        Network.initServer((serverPlayer, itfPacket) -> serverPlayer.playerNetServerHandler.sendPacketToPlayer(itfPacket.toVanilla()));
    }

    private void registerVanillaEvents() {
        Handlers.PlayerEvent.register(new IPlayerEventListener() {
            @Override
            public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
                ServerPlayer player = event.player();
                Network.sendToClient(player, new S2CSyncNutritionLimit(((ServerPlayerAPI) player).getNutritionLimit()));
            }
        });
    }
}
