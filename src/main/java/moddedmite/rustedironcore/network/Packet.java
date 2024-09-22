package moddedmite.rustedironcore.network;

import net.minecraft.EntityPlayer;
import net.minecraft.Packet250CustomPayload;
import net.minecraft.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public interface Packet {
    void write(PacketByteBuf packetByteBuf);

    void apply(EntityPlayer entityPlayer);

    ResourceLocation getChannel();

    default Packet250CustomPayload toVanilla() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        PacketByteBuf buf = PacketByteBuf.out(dos);
        this.write(buf);
        Packet250CustomPayload pkt = new Packet250CustomPayload(this.getChannel().toString(), baos.toByteArray());
        return pkt;
    }
}
