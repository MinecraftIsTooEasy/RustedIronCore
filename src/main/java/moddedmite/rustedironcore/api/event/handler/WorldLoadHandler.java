package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IWorldLoadListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.WorldClient;

@Environment(EnvType.CLIENT)
public class WorldLoadHandler extends AbstractHandler<IWorldLoadListener> {
    public void onWorldUnload(WorldClient worldBefore) {
        this.listeners.forEach(x -> x.onWorldUnload(worldBefore));
    }

    public void onWorldLoad(WorldClient world) {
        this.listeners.forEach(x -> x.onWorldLoad(world));
    }
}
