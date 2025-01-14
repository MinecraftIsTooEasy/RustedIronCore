package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.OreGenerationHandler;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.WorldGenMinable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OreGenerationRegisterEvent {
    public void register(Dimension dimension, WorldGenMinable ore, int frequency) {
        register(dimension, ore, frequency, false);
    }

    public void register(Dimension dimension, WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        Map<Dimension, List<OreGenerationHandler.Setting>> map = Handlers.OreGeneration.ORE_MAP;
        map.computeIfAbsent(dimension, k -> new ArrayList<>());
        map.get(dimension).add(OreGenerationHandler.setting(ore, frequency, increasesWithDepth));
    }
}
