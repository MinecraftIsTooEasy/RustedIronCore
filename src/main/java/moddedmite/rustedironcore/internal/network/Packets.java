package moddedmite.rustedironcore.internal.network;

import moddedmite.rustedironcore.internal.delegate.network.Packet4UpdateTimeDelegate;
import moddedmite.rustedironcore.internal.delegate.network.Packet92UpdateTimeSmallDelegate;
import moddedmite.rustedironcore.network.PacketReader;
import moddedmite.rustedironcore.internal.network.packets.S2COpenGuiTips;
import moddedmite.rustedironcore.internal.network.packets.S2CSyncNutritionLimit;
import moddedmite.rustedironcore.internal.network.packets.S2CSyncTradeRecipe;
import moddedmite.rustedironcore.internal.network.packets.S2CUpdateNutrition;
import moddedmite.rustedironcore.internal.network.packets.C2SRequestChatSuggestions;
import moddedmite.rustedironcore.internal.network.packets.S2CChatSuggestions;
import net.minecraft.ResourceLocation;

public class Packets {
    public static final String CompactID = "RIC";
    public static final ResourceLocation OpenGuiTips = new ResourceLocation(CompactID, "open_gui_tips");
    public static final ResourceLocation SyncTradeRecipe = new ResourceLocation(CompactID, "sync_trade_recipe");
    public static final ResourceLocation SyncNutritionLimit = new ResourceLocation(CompactID, "sync_nutrition_limit");
    public static final ResourceLocation UpdateNutrition = new ResourceLocation(CompactID, "update_nutrition");
    public static final ResourceLocation UpdateTime = new ResourceLocation(CompactID, "update_time");
    public static final ResourceLocation UpdateTimeSmall = new ResourceLocation(CompactID, "update_time_small");
    public static final ResourceLocation RequestChatSuggestions = new ResourceLocation(CompactID, "cmd_request");
    public static final ResourceLocation ChatSuggestions = new ResourceLocation(CompactID, "cmd_suggest");

    public static void registerServerReaders() {
        PacketReader.registerServerPacketReader(RequestChatSuggestions, C2SRequestChatSuggestions::new);
    }

    public static void registerClientReaders() {
        PacketReader.registerClientPacketReader(UpdateNutrition, S2CUpdateNutrition::new);
        PacketReader.registerClientPacketReader(SyncNutritionLimit, S2CSyncNutritionLimit::new);
        PacketReader.registerClientPacketReader(SyncTradeRecipe, S2CSyncTradeRecipe::new);
        PacketReader.registerClientPacketReader(OpenGuiTips, packetByteBuf -> new S2COpenGuiTips());
        PacketReader.registerClientPacketReader(UpdateTime, Packet4UpdateTimeDelegate::new);
        PacketReader.registerClientPacketReader(UpdateTimeSmall, Packet92UpdateTimeSmallDelegate::new);
        PacketReader.registerClientPacketReader(ChatSuggestions, S2CChatSuggestions::new);
    }
}
