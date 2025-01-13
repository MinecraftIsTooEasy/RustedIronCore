package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.MapGenStructure;

public class StructureRegisterEvent {
    public void register(MapGenStructure structure) {
        Handlers.Structure.structures.add(structure);
    }
}
