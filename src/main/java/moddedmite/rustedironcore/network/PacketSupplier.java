package moddedmite.rustedironcore.network;

@FunctionalInterface
public interface PacketSupplier {
    Packet readPacket(PacketByteBuf packetByteBuf);
}
