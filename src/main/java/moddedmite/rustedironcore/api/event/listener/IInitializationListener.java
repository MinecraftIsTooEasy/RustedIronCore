package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.Minecraft;
import net.minecraft.server.MinecraftServer;

public interface IInitializationListener {
    default void onClientStarted(Minecraft client) {
    }

    default void onServerStarted(MinecraftServer server){
    }
}
