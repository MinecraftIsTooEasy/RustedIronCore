package moddedmite.rustedironcore;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.fabricmc.api.ModInitializer;
import net.minecraft.Minecraft;
import net.xiaoyu233.fml.FishModLoader;

public class RustedIronCore implements ModInitializer {
    public static final String MOD_ID = "RustedIronCore";

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
