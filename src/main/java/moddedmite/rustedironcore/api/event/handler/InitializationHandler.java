package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IInitializationListener;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public class InitializationHandler extends AbstractHandler<IInitializationListener> {
    public void onClientStarted(Minecraft client) {
        this.listeners.forEach(x -> x.onClientStarted(client));
    }
    public void onServerStarted(MinecraftServer server) {
        this.listeners.forEach(x -> x.onServerStarted(server));
    }
}
