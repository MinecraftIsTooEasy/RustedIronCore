package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.internal.client.gui.Statement;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public class S2COpenGuiTips implements Packet {

    @Override
    public void write(PacketByteBuf packetByteBuf) {
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        Statement.firstLoginStatementCounter = 600;
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.OpenGuiTips;
    }
}
