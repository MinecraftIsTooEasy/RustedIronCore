package moddedmite.rustedironcore.network;

import moddedmite.rustedironcore.RustedIronCore;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Network {
    public static final String CompactID = "RIC";

    public static final ResourceLocation UpdateNutrition = new ResourceLocation(CompactID, "update_nutrition");

    private static BiConsumer<ServerPlayer, Packet> clientSender;

    private static Consumer<Packet> serverSender;

    public static void initServer(BiConsumer<ServerPlayer, Packet> sender) {
        clientSender = sender;
    }

    public static void initClient(Consumer<Packet> sender) {
        serverSender = sender;
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        clientSender.accept(player, packet);
    }

    public static void sendToServer(Packet packet) {
        serverSender.accept(packet);
    }
}
