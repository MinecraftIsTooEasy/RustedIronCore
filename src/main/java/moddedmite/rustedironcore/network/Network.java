package moddedmite.rustedironcore.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Network {
    private static BiConsumer<ServerPlayer, Packet> clientSender;

    private static Consumer<Packet> serverSender;

    /*
     * Internal api, do not call
     * */
    public static void initServer(BiConsumer<ServerPlayer, Packet> sender) {
        clientSender = sender;
    }

    /*
     * Internal api, do not call
     * */
    public static void initClient(Consumer<Packet> sender) {
        serverSender = sender;
    }

    @Environment(EnvType.SERVER)
    public static void sendToClient(ServerPlayer player, Packet packet) {
        clientSender.accept(player, packet);
    }

    @Environment(EnvType.SERVER)
    public static void sendToAllPlayers(Packet packet) {
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(packet.toVanilla());
    }

    @Environment(EnvType.CLIENT)
    public static void sendToServer(Packet packet) {
        serverSender.accept(packet);
    }
}
