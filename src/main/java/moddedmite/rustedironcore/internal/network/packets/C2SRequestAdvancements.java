package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.internal.advancement.AdvancementNetwork;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;
import net.minecraft.ServerPlayer;

public record C2SRequestAdvancements() implements Packet {

    public static final ResourceLocation CHANNEL = new ResourceLocation("ric", "adv_request");

    @Override
    public void write(PacketByteBuf buffer) {
    }

    @Override
    public void apply(EntityPlayer player) {
        if (player instanceof ServerPlayer serverPlayer) AdvancementNetwork.sendSnapshot(serverPlayer);
    }

    @Override
    public ResourceLocation getChannel() {
        return CHANNEL;
    }
}
