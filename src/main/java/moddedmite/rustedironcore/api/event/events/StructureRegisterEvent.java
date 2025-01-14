package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.MapGenStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StructureRegisterEvent {
    public void register(Dimension dimension, MapGenStructure structure) {
        Map<Dimension, List<MapGenStructure>> map = Handlers.Structure.STRUCTURE_MAP;
        map.computeIfAbsent(dimension, k -> new ArrayList<>());
        map.get(dimension).add(structure);
    }
}
