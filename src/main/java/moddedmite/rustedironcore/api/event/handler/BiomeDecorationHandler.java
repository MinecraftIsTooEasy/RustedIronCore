package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.BiomeDecorationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.World;
import net.minecraft.WorldGenerator;

import java.util.*;

public class BiomeDecorationHandler extends EventHandler<BiomeDecorationRegisterEvent> {
    public final Map<Dimension, List<Setting>> DECORATION_MAP = new HashMap<>();

    public void onDecorate(Context context) {
        World world = context.world;
        Optional<Dimension> optional = DECORATION_MAP.keySet().stream().filter(x -> x.isOf(world)).findFirst();
        if (optional.isEmpty()) return;
        List<Setting> decorations = DECORATION_MAP.get(optional.get());
        decorations.forEach(x -> x.decorate(context));
    }

    public static Context context(World world, Random rand, int blockX, int blockZ) {
        return new Context(world, rand, blockX, blockZ);
    }

    public record Context(World world, Random rand, int blockX, int blockZ) {
    }

    public static Setting setting(WorldGenerator decoration, int attempts, HeightSupplier height) {
        return new Setting(decoration, attempts, height);
    }

    public record Setting(WorldGenerator decoration, int attempts, HeightSupplier height) {
        public void decorate(Context context) {
            int blockX = context.blockX;
            int blockZ = context.blockZ;
            Random rand = context.rand;
            for (int i = 0; i < attempts; i++) {
                int randomX = blockX + rand.nextInt(16) + 8;
                int randomZ = blockZ + rand.nextInt(16) + 8;
                int y = height.getDecorationHeight(context, randomX, randomZ);
                decoration.generate(context.world, rand, randomX, y, randomZ);
            }
        }
    }

    @FunctionalInterface
    public interface HeightSupplier {
        HeightSupplier SURFACE = (context, randomX, randomZ) -> context.world.getHeightValue(randomX, randomZ);
        HeightSupplier COMMON = ((context, randomX, randomZ) -> context.rand.nextInt(128));

        int getDecorationHeight(Context context, int randomX, int randomZ);
    }

}
