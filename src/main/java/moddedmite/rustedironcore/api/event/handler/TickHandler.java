package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public class TickHandler extends AbstractHandler<ITickListener> implements ITickListener {
    public void onEntityPlayerTick(EntityPlayer player) {
        this.listeners.forEach(x -> x.onEntityPlayerTick(player));
    }

    public void onClientTick(Minecraft client) {
        this.listeners.forEach(x -> x.onClientTick(client));
    }

    @Override
    public void onServerTick(MinecraftServer server) {
        this.listeners.forEach(x -> x.onServerTick(server));
    }
}
