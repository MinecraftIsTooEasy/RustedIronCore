package moddedmite.rustedironcore.api.event.events;

import moddedmite.rustedironcore.api.event.handler.BiomeDecorationHandler;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.WorldGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiomeDecorationRegisterEvent {
    private final Map<Dimension, List<BiomeDecorationHandler.SettingBuilder>> map = new HashMap<>();

    public Map<Dimension, List<BiomeDecorationHandler.SettingBuilder>> getMap() {
        return this.map;
    }

    public BiomeDecorationHandler.SettingBuilder register(Dimension dimension, WorldGenerator decoration) {
        BiomeDecorationHandler.SettingBuilder args = new BiomeDecorationHandler.SettingBuilder(decoration);
        this.map.computeIfAbsent(dimension, k -> new ArrayList<>()).add(args);
        return args;
    }

    @Deprecated
    public BiomeDecorationHandler.SettingBuilder register(Dimension dimension, WorldGenerator decoration, int attempts) {
        return register(dimension, decoration).setAttempts(attempts);
    }

    @Deprecated
    public BiomeDecorationHandler.SettingBuilder register(Dimension dimension, WorldGenerator decoration, int attempts, BiomeDecorationHandler.HeightSupplier height) {
        return register(dimension, decoration).setAttempts(attempts).setHeightSupplier(height);
    }
}
