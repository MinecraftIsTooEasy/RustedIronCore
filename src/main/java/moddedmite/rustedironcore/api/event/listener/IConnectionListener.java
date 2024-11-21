package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.NetClientHandler;
import net.minecraft.Packet1Login;
import net.minecraft.Packet255KickDisconnect;
import net.minecraft.server.MinecraftServer;

public interface IConnectionListener {
    default void onClientConnection(NetClientHandler clientHandler, String server, int port) {
    }

    default void onIntegratedConnection(NetClientHandler clientHandler, MinecraftServer server) {
    }

    default void onClientLoggedIn(NetClientHandler clientHandler, Packet1Login login) {
    }

    default void onClientQuit(NetClientHandler clientHandler, Packet255KickDisconnect disconnect) {
    }
}
