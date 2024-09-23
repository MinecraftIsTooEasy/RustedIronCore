package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;

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
        clientPlayer.setPhytonutrients(this.phytonutrients);
        clientPlayer.setProtein(this.protein);
        clientPlayer.setEssentialFats(this.essential_fats);
    }

    @Override
    public ResourceLocation getChannel() {
        return Network.UpdateNutrition;
    }
}

