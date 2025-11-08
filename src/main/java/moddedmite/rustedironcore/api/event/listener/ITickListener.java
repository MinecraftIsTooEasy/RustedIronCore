package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public interface ITickListener {
    default void onEntityPlayerTick(EntityPlayer player) {
    }

    @Environment(EnvType.CLIENT)
    default void onClientTick(Minecraft client) {
    }

    @Environment(EnvType.CLIENT)
    default void onServerTick(MinecraftServer server) {
    }

    // Called every frame
    @Environment(EnvType.CLIENT)
    default void onRenderTick(float partialTick) {
    }

    default void onEntityTick(Entity entity) {
    }
}
