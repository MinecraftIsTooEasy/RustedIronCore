package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.WorldClient;

public interface IWorldLoadListener {
    default void onWorldUnload(WorldClient worldBefore) {
    }

    default void onWorldLoad(WorldClient world) {
    }
}
