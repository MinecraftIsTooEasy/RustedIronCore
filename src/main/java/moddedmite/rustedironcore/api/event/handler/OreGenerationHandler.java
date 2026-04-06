package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.BiomeDecorator;
import net.minecraft.World;
import net.minecraft.WorldGenMinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OreGenerationHandler extends EventHandler<OreGenerationRegisterEvent> {
    private final Map<Dimension, List<Setting>> ORE_MAP = new HashMap<>();

    @ApiStatus.Internal
    public void onOresGeneration(Context context) {
        World world = context.world;
        Optional<Dimension> optional = ORE_MAP.keySet().stream().filter(x -> x.isOf(world)).findFirst();
        if (optional.isEmpty()) return;
        ORE_MAP.get(optional.get()).forEach(x -> x.generate(context));
    }

    @ApiStatus.Internal
    public void registerOre(Dimension dimension, WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        this.ORE_MAP.computeIfAbsent(dimension, k -> new ArrayList<>())
                .add(setting(ore, frequency, increasesWithDepth));
    }

    @ApiStatus.Internal
    public void unregisterOre(Dimension dimension, int blockId) {
        Map<Dimension, List<Setting>> map = this.ORE_MAP;
        if (map.containsKey(dimension)) {
            List<Setting> list = map.get(dimension);
            list.removeIf(setting -> setting.ore.getMinableBlockId() == blockId);
        }
    }

    public static Context context(World world, @Nullable BiomeDecorator biomeDecorator, Random random, int blockX, int blockZ) {
        return new Context(world, biomeDecorator, random, blockX, blockZ);
    }

    public record Context(World world, @Nullable BiomeDecorator biomeDecorator, Random random, int blockX, int blockZ) {
    }

    private static Setting setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        return new Setting(ore, frequency, increasesWithDepth);
    }

    private record Setting(WorldGenMinable ore, int frequency, boolean increasesWithDepth) {
        private void generate(Context context) {
            BiomeDecorator decorator = context.biomeDecorator;
            if (decorator != null) {
                decorator.genMinable(frequency, ore, increasesWithDepth);
                return;
            }
            World world = context.world;
            Random random = context.random;
            int blockX = context.blockX;
            int blockZ = context.blockZ;
            for (int i = 0; i < frequency; i++) {
                int x = blockX + random.nextInt(16);
                int y = ore.getRandomVeinHeight(world, random);
                int z = blockZ + random.nextInt(16);
                ore.generate(world, random, x, y, z);
            }
        }
    }
}
