package moddedmite.rustedironcore.internal.delegate.network;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;

public class Packet4UpdateTimeDelegate implements Packet {
    private final long[] array;

    public Packet4UpdateTimeDelegate(long[] array) {
        this.array = array;
    }

    public Packet4UpdateTimeDelegate(MinecraftServer server) {
        this(createArray(server));
    }

    public Packet4UpdateTimeDelegate(PacketByteBuf packetByteBuf) {
        this(createArray(packetByteBuf));
    }

    private static long[] createArray(MinecraftServer server) {
        boolean use_small_packet_instead = true;
        WorldServer[] worlds = server.worldServers;
        int length = worlds.length;
        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            long time = worlds[i].getTotalWorldTime();
            if (!Packet92UpdateTimeSmall.isTimeSuitable(time)) {
                use_small_packet_instead = false;
            }
            array[i] = time;
        }
        if (use_small_packet_instead) {
            Minecraft.setErrorMessage("Packet4UpdateTime: use Packet92UpdateTimeSmall instead");
            new Exception().printStackTrace();
        }
        return array;
    }

    private static long[] createArray(PacketByteBuf packetByteBuf) {
        int count = Handlers.Dimension.getTotalDimensionsCount();
        long[] array = new long[count];
        for (int i = 0; i < count; i++) {
            array[i] = packetByteBuf.readInt();
        }
        return array;
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) {
        for (int i = 0; i < this.array.length; i++) {
            packetByteBuf.writeLong(this.array[i]);
        }
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        Minecraft client = Minecraft.getMinecraft();
        client.theWorld.getWorldInfo().setTotalWorldTimes(this.array, client.theWorld);
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.UpdateTime;
    }
}
