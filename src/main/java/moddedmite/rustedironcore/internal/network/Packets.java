package moddedmite.rustedironcore.internal.network;

import moddedmite.rustedironcore.delegate.network.Packet4UpdateTimeDelegate;
import moddedmite.rustedironcore.delegate.network.Packet92UpdateTimeSmallDelegate;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.network.packets.S2CSyncNutritionLimit;
import moddedmite.rustedironcore.network.packets.S2CSyncTradeRecipe;
import moddedmite.rustedironcore.network.packets.S2CUpdateNutrition;
import net.minecraft.ResourceLocation;

public class Packets {
    public static final String CompactID = "RIC";
    public static final ResourceLocation OpenGuiTips = new ResourceLocation(CompactID, "open_gui_tips");
    public static final ResourceLocation SyncTradeRecipe = new ResourceLocation(CompactID, "sync_trade_recipe");
    public static final ResourceLocation SyncNutritionLimit = new ResourceLocation(CompactID, "sync_nutrition_limit");
    public static final ResourceLocation UpdateNutrition = new ResourceLocation(CompactID, "update_nutrition");
    public static final ResourceLocation UpdateTime = new ResourceLocation(CompactID, "update_time");
    public static final ResourceLocation UpdateTimeSmall = new ResourceLocation(CompactID, "update_time_small");

    public static void registerServerReaders() {
    }

    public static void registerClientReaders() {
        PacketReader.registerClientPacketReader(UpdateNutrition, S2CUpdateNutrition::new);
        PacketReader.registerClientPacketReader(SyncNutritionLimit, S2CSyncNutritionLimit::new);
        PacketReader.registerClientPacketReader(SyncTradeRecipe, S2CSyncTradeRecipe::new);
        PacketReader.registerClientPacketReader(OpenGuiTips, packetByteBuf -> new S2COpenGuiTips());
        PacketReader.registerClientPacketReader(UpdateTime, Packet4UpdateTimeDelegate::new);
        PacketReader.registerClientPacketReader(UpdateTimeSmall, Packet92UpdateTimeSmallDelegate::new);
    }
}
