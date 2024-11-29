package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public class TickHandler extends AbstractHandler<ITickListener> implements ITickListener {
    @Override
    public void onEntityPlayerTick(EntityPlayer player) {
        this.listeners.forEach(x -> x.onEntityPlayerTick(player));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onClientTick(Minecraft client) {
        this.listeners.forEach(x -> x.onClientTick(client));
    }

    @Environment(EnvType.SERVER)
    @Override
    public void onServerTick(MinecraftServer server) {
        this.listeners.forEach(x -> x.onServerTick(server));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void onRenderTick(float partialTick) {
        ITickListener.super.onRenderTick(partialTick);
    }
}
