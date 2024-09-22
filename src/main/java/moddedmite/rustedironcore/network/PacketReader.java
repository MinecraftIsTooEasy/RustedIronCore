package moddedmite.rustedironcore.network;

import net.minecraft.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PacketReader {
    public static final Map<String, PacketSupplier> clientReaders = new HashMap<>();

    public static final Map<String, PacketSupplier> serverReaders = new HashMap<>();

    public static void registerServerPacketReader(ResourceLocation id, PacketSupplier reader) {
        serverReaders.put(id.toString(), reader);
    }

    public static void registerClientPacketReader(ResourceLocation id, PacketSupplier reader) {
        clientReaders.put(id.toString(), reader);
    }

}
