package moddedmite.rustedironcore.network.packets;

import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class S2COpenGuiTips implements Packet {
    public static int firstLoginStatementCounter = 0;

    @Override
    public void write(PacketByteBuf packetByteBuf) {
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        firstLoginStatementCounter = 600;
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.OpenGuiTips;
    }
}
