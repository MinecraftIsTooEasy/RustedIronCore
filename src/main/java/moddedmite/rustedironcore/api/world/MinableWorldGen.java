package moddedmite.rustedironcore.api.world;

import net.minecraft.World;
import net.minecraft.WorldGenMinable;

import java.util.Random;

/**
 * This class solves the problem of vein height hardcoded in the super class.
 */
public class MinableWorldGen extends WorldGenMinable {
    VeinHeightSupplier maxVeinHeight;
    VeinHeightSupplier minVeinHeight;
    RandomVeinHeightSupplier randomVeinHeight;

    public static final RandomVeinHeightSupplier Common = (world, rand, minable) -> {
        float relative_height;
        do {
            relative_height = rand.nextFloat();
        } while (relative_height >= rand.nextFloat());
        int min_height = minable.getMinVeinHeight(world);
        int height_range = minable.getMaxVeinHeight(world) - min_height + 1;
        return min_height + (int) (relative_height * (float) height_range);
    };

    public MinableWorldGen(int minableBlockId, int numberOfBlocks) {
        super(minableBlockId, numberOfBlocks);
    }

    public MinableWorldGen(int minableBlockId, int numberOfBlocks, int blockToReplace) {
        super(minableBlockId, numberOfBlocks, blockToReplace);
    }

    public MinableWorldGen setMaxVeinHeight(VeinHeightSupplier maxVeinHeight) {
        this.maxVeinHeight = maxVeinHeight;
        return this;
    }

    @Override
    public int getMaxVeinHeight(World world) {
        return this.maxVeinHeight.getVeinHeight(world, this);
    }

    public MinableWorldGen setMinVeinHeight(VeinHeightSupplier minVeinHeight) {
        this.minVeinHeight = minVeinHeight;
        return this;
    }

    @Override
    public int getMinVeinHeight(World world) {
        return this.minVeinHeight.getVeinHeight(world, this);
    }

    public MinableWorldGen setRandomVeinHeight(RandomVeinHeightSupplier randomVeinHeight) {
        this.randomVeinHeight = randomVeinHeight;
        return this;
    }

    @Override
    public int getRandomVeinHeight(World world, Random rand) {
        return this.randomVeinHeight.getVeinHeight(world, rand, this);
    }

    @FunctionalInterface
    public interface VeinHeightSupplier {
        int getVeinHeight(World world, MinableWorldGen minable);
    }

    @FunctionalInterface
    public interface RandomVeinHeightSupplier {
        int getVeinHeight(World world, Random rand, MinableWorldGen minable);
    }
}
