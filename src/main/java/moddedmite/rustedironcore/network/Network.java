package moddedmite.rustedironcore.network;

import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import moddedmite.rustedironcore.network.packets.S2CSyncTradeRecipe;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Network {
    public static final String CompactID = "RIC";

    public static final ResourceLocation UpdateNutrition = new ResourceLocation(CompactID, "update_nutrition");
    public static final ResourceLocation SyncNutritionLimit = new ResourceLocation(CompactID, "sync_nutrition_limit");
    public static final ResourceLocation SyncTradeRecipe = new ResourceLocation(CompactID, "sync_trade_recipe");
    public static final ResourceLocation OpenGuiTips = new ResourceLocation(CompactID, "open_gui_tips");

    private static BiConsumer<ServerPlayer, Packet> clientSender;

    private static Consumer<Packet> serverSender;

    public static void initServer(BiConsumer<ServerPlayer, Packet> sender) {
        clientSender = sender;
        registerServerReaders();
    }

    public static void initClient(Consumer<Packet> sender) {
        serverSender = sender;
        registerClientReaders();
    }

    public static void sendToClient(ServerPlayer player, Packet packet) {
        clientSender.accept(player, packet);
    }

    public static void sendToServer(Packet packet) {
        serverSender.accept(packet);
    }

    private static void registerServerReaders() {
    }

    private static void registerClientReaders() {
        PacketReader.registerClientPacketReader(UpdateNutrition, S2CUpdateNutrition::new);
        PacketReader.registerClientPacketReader(SyncNutritionLimit, S2CSyncNutritionLimit::new);
        PacketReader.registerClientPacketReader(SyncTradeRecipe, S2CSyncTradeRecipe::new);
        PacketReader.registerClientPacketReader(OpenGuiTips, packetByteBuf -> new S2COpenGuiTips());
    }
}
