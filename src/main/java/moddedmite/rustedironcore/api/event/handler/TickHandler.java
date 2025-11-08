package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public class TickHandler extends AbstractHandler<ITickListener> {
    public void onEntityPlayerTick(EntityPlayer player) {
        this.listeners.forEach(x -> x.onEntityPlayerTick(player));
    }

    @Environment(EnvType.CLIENT)
    public void onClientTick(Minecraft client) {
        this.listeners.forEach(x -> x.onClientTick(client));
    }

    @Environment(EnvType.SERVER)
    public void onServerTick(MinecraftServer server) {
        this.listeners.forEach(x -> x.onServerTick(server));
    }

    @Environment(EnvType.CLIENT)
    public void onRenderTick(float partialTick) {
        this.listeners.forEach(x -> x.onRenderTick(partialTick));
    }

    public void onEntityTick(Entity entity) {
        this.listeners.forEach(x -> x.onEntityTick(entity));
    }
}
