package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.BiomeDecorationHandler;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BiomeDecorationRegisterEvent {
    public void register(Dimension dimension, WorldGenerator decoration) {
        register(dimension, decoration, 1);
    }

    public void register(Dimension dimension, WorldGenerator decoration, int attempts) {
        register(dimension, decoration, attempts, BiomeDecorationHandler.HeightSupplier.COMMON);
    }

    public void register(Dimension dimension, WorldGenerator decoration, int attempts, BiomeDecorationHandler.HeightSupplier height) {
        Map<Dimension, List<BiomeDecorationHandler.Setting>> map = Handlers.BiomeDecoration.DECORATION_MAP;
        map.computeIfAbsent(dimension, k -> new ArrayList<>());
        map.get(dimension).add(BiomeDecorationHandler.setting(decoration, attempts, height));
    }
}
