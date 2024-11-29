package moddedmite.rustedironcore.api.event.listener;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.WorldClient;

@Environment(EnvType.CLIENT)
public interface IWorldLoadListener {
    default void onWorldUnload(WorldClient worldBefore) {
    }

    default void onWorldLoad(WorldClient world) {
    }
}
