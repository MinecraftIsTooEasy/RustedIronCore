package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.OreGenerationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.BiomeDecorator;
import net.minecraft.World;
import net.minecraft.WorldGenMinable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.ToIntFunction;

public class OreGenerationHandler extends EventHandler<OreGenerationRegisterEvent> {
    private final Map<Dimension, List<Setting>> ORE_MAP = new HashMap<>();

    public static final ToIntFunction<Random> NETHER_ORE_HEIGHT = r -> r.nextInt(108) + 10;
    private final Map<Dimension, ToIntFunction<Random>> ORE_HEIGHT_MAP = new HashMap<>();

    public OreGenerationHandler() {
        this.ORE_HEIGHT_MAP.put(Dimension.NETHER, NETHER_ORE_HEIGHT);
    }

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

    public void setDimensionOreHeight(Dimension dimension, ToIntFunction<Random> height) {
        this.ORE_HEIGHT_MAP.put(dimension, height);
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
            Dimension dimension = Dimension.fromWorld(world);
            ToIntFunction<Random> oreHeight;
            if (dimension != null && Handlers.OreGeneration.ORE_HEIGHT_MAP.containsKey(dimension)) {
                oreHeight = Handlers.OreGeneration.ORE_HEIGHT_MAP.get(dimension);
            } else {
                oreHeight = NETHER_ORE_HEIGHT;
            }
            Random random = context.random;
            int blockX = context.blockX;
            int blockZ = context.blockZ;
            for (int i = 0; i < frequency; i++) {
                int x = blockX + random.nextInt(16);
                int y = oreHeight.applyAsInt(random);
                int z = blockZ + random.nextInt(16);
                ore.generate(world, random, x, y, z);
            }
        }
    }
}
