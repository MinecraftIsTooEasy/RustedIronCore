package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.EntityPlayer;
import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public interface ITickListener {
    default void onEntityPlayerTick(EntityPlayer player) {
    }

    default void onClientTick(Minecraft client) {
    }

    default void onServerTick(MinecraftServer server) {
    }
}
