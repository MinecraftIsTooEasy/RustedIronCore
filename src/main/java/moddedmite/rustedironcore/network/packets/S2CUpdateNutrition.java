package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityClientPlayerMP;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class S2CUpdateNutrition implements Packet {
    private final int phytonutrients;
    private final int protein;
    private final int essential_fats;

    public S2CUpdateNutrition(int phytonutrients, int protein, int essential_fats) {
        this.phytonutrients = phytonutrients;
        this.protein = protein;
        this.essential_fats = essential_fats;
    }

    public S2CUpdateNutrition(PacketByteBuf packetByteBuf) {// order sensitive
        this(packetByteBuf.readInt(), packetByteBuf.readInt(), packetByteBuf.readInt());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(this.phytonutrients);
        packetByteBuf.writeInt(this.protein);
        packetByteBuf.writeInt(this.essential_fats);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        EntityClientPlayerMP clientPlayer = (EntityClientPlayerMP) entityPlayer;
        clientPlayer.ric$SetPhytonutrients(this.phytonutrients);
        clientPlayer.ric$SetProtein(this.protein);
        clientPlayer.ric$SetEssentialFats(this.essential_fats);
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.UpdateNutrition;
    }
}

