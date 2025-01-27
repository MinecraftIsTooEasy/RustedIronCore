package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.BiomeDecorationRegisterEvent;
import moddedmite.rustedironcore.api.world.Dimension;
import net.minecraft.BiomeDecorator;
import net.minecraft.BiomeGenBase;
import net.minecraft.World;
import net.minecraft.WorldGenerator;

import java.util.*;

public class BiomeDecorationHandler extends EventHandler<BiomeDecorationRegisterEvent> {
    private final Map<Dimension, List<Setting>> DECORATION_MAP = new HashMap<>();

    public void buildMap(Map<Dimension, List<SettingBuilder>> builderMap) {
        for (Map.Entry<Dimension, List<BiomeDecorationHandler.SettingBuilder>> entry : builderMap.entrySet()) {
            this.DECORATION_MAP.put(entry.getKey(), entry.getValue().stream().map(BiomeDecorationHandler.SettingBuilder::build).toList());
        }
    }

    public void onDecorate(Context context) {
        World world = context.world;
        Optional<Dimension> optional = DECORATION_MAP.keySet().stream().filter(x -> x.isOf(world)).findFirst();
        if (optional.isEmpty()) return;
        List<Setting> decorations = DECORATION_MAP.get(optional.get());
        decorations.forEach(x -> x.decorate(context));
    }

    public static Context context(World world, BiomeGenBase biome, BiomeDecorator biomeDecorator, Random rand, int blockX, int blockZ) {
        return new Context(world, biome, biomeDecorator, rand, blockX, blockZ);
    }

    /**
     * @param blockX The west-north corner of the chunk.
     * @param blockZ The west-north corner of the chunk.
     */
    public record Context(World world, BiomeGenBase biome, BiomeDecorator biomeDecorator, Random rand, int blockX,
                          int blockZ) {
    }

    public record Setting(WorldGenerator decoration, Frequency frequency, HeightSupplier height) {
        public void decorate(Context context) {
            int blockX = context.blockX;
            int blockZ = context.blockZ;
            Random rand = context.rand;
            for (int i = 0; i < frequency.get(context); i++) {
                int randomX = blockX + rand.nextInt(16) + 8;
                int randomZ = blockZ + rand.nextInt(16) + 8;
                int y = height.getDecorationHeight(context, randomX, randomZ);
                decoration.generate(context.world, rand, randomX, y, randomZ);
            }
        }
    }

    public static class SettingBuilder {
        private final WorldGenerator decoration;

        private Frequency frequency = Frequency.ONCE;

        private HeightSupplier heightSupplier = HeightSupplier.COMMON;

        public SettingBuilder(WorldGenerator decoration) {
            this.decoration = decoration;
        }

        public SettingBuilder setAttempts(int attempts) {
            this.frequency = context -> attempts;
            return this;
        }

        public SettingBuilder setChance(int chance) {
            this.frequency = context -> context.rand().nextInt(chance) == 0 ? 1 : 0;
            return this;
        }

        public SettingBuilder setFrequency(Frequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public SettingBuilder setSurface() {
            this.heightSupplier = HeightSupplier.SURFACE;
            return this;
        }

        public SettingBuilder setHeightSupplier(HeightSupplier heightSupplier) {
            this.heightSupplier = heightSupplier;
            return this;
        }

        public Setting build() {
            return new Setting(decoration, frequency, heightSupplier);
        }
    }

    @FunctionalInterface
    public interface Frequency {
        Frequency ONCE = context -> 1;

        int get(Context context);
    }

    @FunctionalInterface
    public interface HeightSupplier {
        HeightSupplier SURFACE = (context, randomX, randomZ) -> context.world().getHeightValue(randomX, randomZ);
        HeightSupplier COMMON = ((context, randomX, randomZ) -> context.rand().nextInt(128));

        /**
         * @param randomX the chosen random position in the chunk.
         * @param randomZ the chosen random position in the chunk.
         */
        int getDecorationHeight(Context context, int randomX, int randomZ);
    }
}
