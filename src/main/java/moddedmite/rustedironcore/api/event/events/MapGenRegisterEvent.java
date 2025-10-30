package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.MapGenBase;
import net.minecraft.MapGenStructure;

public class MapGenRegisterEvent {
    public void register(Dimension dimension, MapGenBase mapGen) {
        Handlers.MapGen.register(dimension, mapGen);
    }

    public void registerStructure(Dimension dimension, MapGenStructure structure) {
        Handlers.MapGen.registerStructure(dimension, structure);
    }
}
