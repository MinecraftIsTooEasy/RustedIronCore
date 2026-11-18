package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.internal.advancement.AdvancementNetwork;
import moddedmite.rustedironcore.internal.client.advancement.ClientAdvancements;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

public record S2CAdvancements(int transfer, int index, boolean last, byte[] bytes) implements Packet {

    public static final ResourceLocation CHANNEL = new ResourceLocation("ric", "advancements");

    public S2CAdvancements(PacketByteBuf buffer) {
        this(buffer.readInt(), buffer.readInt(), buffer.readBoolean(), readPayload(buffer));
    }

    private static byte[] readPayload(PacketByteBuf buffer) {
        int length = buffer.readVarInt();
        if (length < 0 || length > AdvancementNetwork.FRAGMENT_SIZE) throw new IllegalArgumentException("Invalid advancement fragment length");
        byte[] bytes = new byte[length];
        buffer.readFully(bytes);
        return bytes;
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.writeInt(transfer);
        buffer.writeInt(index);
        buffer.writeBoolean(last);
        buffer.writeVarInt(bytes.length);
        buffer.write(bytes, 0, bytes.length);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void apply(EntityPlayer player) {
        ClientAdvancements.receive(this, player);
    }

    @Override
    public ResourceLocation getChannel() {
        return CHANNEL;
    }
}
