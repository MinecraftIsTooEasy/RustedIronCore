package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.api.player.ClientPlayerAPI;
import moddedmite.rustedironcore.internal.network.Packets;
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
        ((ClientPlayerAPI) (EntityClientPlayerMP) entityPlayer).ric$SetNutritionLimit(this.limit);
    }// this double cast is necessary

    @Override
    public ResourceLocation getChannel() {
        return Packets.SyncNutritionLimit;
    }
}
