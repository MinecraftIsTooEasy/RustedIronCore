package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;

public class S2CSyncTradeRecipe implements Packet {
    private final int windowID;

    private final MerchantRecipeList list;

    public S2CSyncTradeRecipe(int windowID, MerchantRecipeList list) {
        this.windowID = windowID;
        this.list = list;
    }

    public S2CSyncTradeRecipe(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readInt(), packetByteBuf.readMerchantRecipeList());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(this.windowID);
        packetByteBuf.writeMerchantRecipeList(this.list);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        GuiScreen var4 = Minecraft.getMinecraft().currentScreen;
        if (var4 instanceof GuiMerchant guiMerchant && this.windowID == Minecraft.getMinecraft().thePlayer.openContainer.windowId) {
            IMerchant var5 = guiMerchant.getIMerchant();
            var5.setRecipes(this.list);
            ((ContainerMerchant) guiMerchant.inventorySlots).getMerchantInventory().resetRecipeAndSlots();
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.SyncTradeRecipe;
    }
}
