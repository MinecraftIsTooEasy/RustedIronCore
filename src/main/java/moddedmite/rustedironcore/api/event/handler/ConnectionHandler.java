package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IConnectionListener;
import net.minecraft.NetClientHandler;
import net.minecraft.Packet1Login;
import net.minecraft.Packet255KickDisconnect;
import net.minecraft.server.MinecraftServer;

public class ConnectionHandler extends AbstractHandler<IConnectionListener> {
    /*
     * Steps:
     * 1. connection
     * 2. log in
     * 3. gameplay
     * 4. quit
     * */

    public void onClientConnection(NetClientHandler clientHandler, String server, int port) {
        this.listeners.forEach(x -> x.onClientConnection(clientHandler, server, port));
    }

    public void onIntegratedConnection(NetClientHandler clientHandler, MinecraftServer server) {
        this.listeners.forEach(x -> x.onIntegratedConnection(clientHandler, server));
    }

    public void onClientLoggedIn(NetClientHandler clientHandler, Packet1Login login) {
        this.listeners.forEach(x -> x.onClientLoggedIn(clientHandler, login));
    }

    public void onClientQuit(NetClientHandler clientHandler, Packet255KickDisconnect disconnect) {
        this.listeners.forEach(x -> x.onClientQuit(clientHandler, disconnect));
    }
}
