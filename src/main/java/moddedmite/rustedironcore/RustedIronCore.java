package moddedmite.rustedironcore;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
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
    }

    private void initClient() {
        Network.initClient(packet -> Minecraft.getMinecraft().getNetHandler().addToSendQueue(packet.toVanilla()));
        PacketReader.registerClientPacketReader(Network.UpdateNutrition, S2CUpdateNutrition::new);
    }

    private void initServer() {
        Network.initServer((serverPlayer, itfPacket) -> serverPlayer.playerNetServerHandler.sendPacketToPlayer(itfPacket.toVanilla()));
    }
}
