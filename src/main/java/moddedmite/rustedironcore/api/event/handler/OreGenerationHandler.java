package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.BiomeDecorator;
import net.minecraft.World;
import net.minecraft.WorldGenMinable;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class OreGenerationHandler extends EventHandler<OreGenerationRegisterEvent> {
    private final Map<Dimension, List<Setting>> ORE_MAP = new HashMap<>();

    @ApiStatus.Internal
    public void onOresGeneration(Context context) {
        World world = context.world;
        Optional<Dimension> optional = ORE_MAP.keySet().stream().filter(x -> x.isOf(world)).findFirst();
        if (optional.isEmpty()) return;
        List<Setting> ores = ORE_MAP.get(optional.get());
        BiomeDecorator biomeDecorator = context.biomeDecorator;
        ores.forEach(x -> x.generate(biomeDecorator));
    }

    @ApiStatus.Internal
    public void registerOre(Dimension dimension, WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        Map<Dimension, List<Setting>> map = this.ORE_MAP;
        map.computeIfAbsent(dimension, k -> new ArrayList<>());
        map.get(dimension).add(setting(ore, frequency, increasesWithDepth));
    }

    @ApiStatus.Internal
    public void unregisterOre(Dimension dimension, int blockId) {
        Map<Dimension, List<Setting>> map = this.ORE_MAP;
        if (map.containsKey(dimension)) {
            List<Setting> list = map.get(dimension);
            list.removeIf(setting -> setting.ore.getMinableBlockId() == blockId);
        }
    }

    public static Context context(BiomeDecorator biomeDecorator, World world) {
        return new Context(biomeDecorator, world);
    }

    public record Context(BiomeDecorator biomeDecorator, World world) {
    }

    private static Setting setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        return new Setting(ore, frequency, increasesWithDepth);
    }

    private record Setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        private void generate(BiomeDecorator biomeDecorator) {
            biomeDecorator.genMinable(frequency, ore, increasesWithDepth);
        }
    }
}
