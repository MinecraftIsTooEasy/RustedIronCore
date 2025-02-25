package moddedmite.rustedironcore.delegate.network;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.internal.network.Packets;
import moddedmite.rustedironcore.network.Packet;
import moddedmite.rustedironcore.network.PacketByteBuf;
import net.minecraft.*;
import net.minecraft.server.MinecraftServer;

public class Packet92UpdateTimeSmallDelegate implements Packet {
    private final long[] array;

    public Packet92UpdateTimeSmallDelegate(long[] array) {
        this.array = array;
    }

    public Packet92UpdateTimeSmallDelegate(MinecraftServer server) {
        this(createArray(server));
    }

    public Packet92UpdateTimeSmallDelegate(PacketByteBuf packetByteBuf) {
        this(createArray(packetByteBuf));
    }

    private static long[] createArray(MinecraftServer server) {
        WorldServer[] worlds = server.worldServers;
        int length = worlds.length;
        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            long time = worlds[i].getTotalWorldTime();
            if (Packet92UpdateTimeSmall.isTimeSuitable(time)) {
                array[i] = time;
                continue;
            }
            Minecraft.setErrorMessage("Packet92UpdateTimeSmall: time is too large!");
            array[i] = Packet92UpdateTimeSmall.getLargestSuitableTime();
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
            packetByteBuf.writeInt(((int) this.array[i]));
        }
    }

    @Override
    public void apply(EntityPlayer entityPlayer) {
        Minecraft client = Minecraft.getMinecraft();
        client.theWorld.getWorldInfo().setTotalWorldTimes(this.array, client.theWorld);
    }

    @Override
    public ResourceLocation getChannel() {
        return Packets.UpdateTimeSmall;
    }
}
