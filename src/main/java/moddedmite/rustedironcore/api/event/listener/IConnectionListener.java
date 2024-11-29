package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.NetClientHandler;
import net.minecraft.Packet1Login;
import net.minecraft.Packet255KickDisconnect;
import net.minecraft.server.MinecraftServer;

public interface IConnectionListener {
    @Environment(EnvType.CLIENT)
    default void onClientConnection(NetClientHandler clientHandler, String server, int port) {
    }

    default void onIntegratedConnection(NetClientHandler clientHandler, MinecraftServer server) {
    }

    @Environment(EnvType.CLIENT)
    default void onClientLoggedIn(NetClientHandler clientHandler, Packet1Login login) {
    }

    @Environment(EnvType.CLIENT)
    default void onClientQuit(NetClientHandler clientHandler, Packet255KickDisconnect disconnect) {
    }
}
