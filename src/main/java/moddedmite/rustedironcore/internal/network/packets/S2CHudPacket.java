package moddedmite.rustedironcore.internal.network.packets;

import moddedmite.rustedironcore.api.hud.HudChannel;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import moddedmite.rustedironcore.internal.hud.HudOperation;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

public final class S2CHudPacket implements Packet {

    private final HudChannel channel;
    private final byte[] payload;

    public S2CHudPacket(HudChannel channel, HudOperation operation, Consumer<PacketByteBuf> writer) {
        this.channel = channel;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PacketByteBuf buffer = PacketByteBuf.out(new DataOutputStream(bytes));
        buffer.writeByte(operation.ordinal());
        writer.accept(buffer);
        payload = bytes.toByteArray();
    }

    public S2CHudPacket(HudChannel channel, PacketByteBuf buffer) {
        this.channel = channel;
        try {
            payload = buffer.getInputStream().readAllBytes();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public void write(PacketByteBuf buffer) {
        buffer.write(payload, 0, payload.length);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void apply(EntityPlayer player) {
        ClientHud.receive(channel, player, PacketByteBuf.in(new DataInputStream(new ByteArrayInputStream(payload))));
    }

    @Override
    public ResourceLocation getChannel() {
        return channel.getId();
    }
}
