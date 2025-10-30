package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.MapGenStructure;

public class StructureRegisterEvent {
    @Deprecated(since = "1.4.1")
    public void register(Dimension dimension, MapGenStructure structure) {
        Handlers.MapGen.registerStructure(dimension, structure);
    }
}
