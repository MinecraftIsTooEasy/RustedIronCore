package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.api.player.ClientPlayerAPI;
import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityClientPlayerMP;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class S2CSyncNutritionLimit implements Packet {
    private final int limit;

    public S2CSyncNutritionLimit(int limit) {
        this.limit = limit;
    }

    public S2CSyncNutritionLimit(PacketByteBuf packetByteBuf) {
        this(packetByteBuf.readInt());
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        packetByteBuf.writeInt(this.limit);
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        ((ClientPlayerAPI) (EntityClientPlayerMP) entityPlayer).setNutritionLimit(this.limit);
    }// this double cast is necessary

    @Override
    public ResourceLocation getChannel() {
        return Network.SyncNutritionLimit;
    }
}
