package moddedmite.rustedironcore.api.model;

import net.minecraft.Block;
import net.minecraft.IBlockAccess;

/** Samples MITE's packed world light for JSON model faces. */
final class WorldLightSampler {

    private static final int BLOCK_LIGHT_MASK = 0x0000F0;
    private static final int SKY_LIGHT_MASK = 0xF00000;
    private static final double EPSILON = 1.0E-5D;

    private final IBlockAccess blockAccess;
    private final Block block;
    private final int x;
    private final int y;
    private final int z;
    private final int interiorBrightness;

    WorldLightSampler(IBlockAccess blockAccess, Block block, int x, int y, int z) {
        this.blockAccess = blockAccess;
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
        this.interiorBrightness = this.sampleInterior();
    }

    int sampleFace(double[][] vertices, JsonModelDirection facing) {
        if (!isBoundaryPlane(vertices, facing, this.x, this.y, this.z)) {
            return this.interiorBrightness;
        }

        return switch (facing) {
            case DOWN -> this.sample(this.x, this.y - 1, this.z);
            case UP -> this.sample(this.x, this.y + 1, this.z);
            case NORTH -> this.sample(this.x, this.y, this.z - 1);
            case SOUTH -> this.sample(this.x, this.y, this.z + 1);
            case WEST -> this.sample(this.x - 1, this.y, this.z);
            case EAST -> this.sample(this.x + 1, this.y, this.z);
        };
    }

    private int sampleInterior() {
        int brightness = this.sample(this.x, this.y, this.z);
        if (!this.block.isNeverOpaqueStandardFormCube()) {
            return brightness;
        }

        brightness = combineBrightness(brightness, this.sample(this.x, this.y - 1, this.z));
        brightness = combineBrightness(brightness, this.sample(this.x, this.y + 1, this.z));
        brightness = combineBrightness(brightness, this.sample(this.x, this.y, this.z - 1));
        brightness = combineBrightness(brightness, this.sample(this.x, this.y, this.z + 1));
        brightness = combineBrightness(brightness, this.sample(this.x - 1, this.y, this.z));
        return combineBrightness(brightness, this.sample(this.x + 1, this.y, this.z));
    }

    private int sample(int x, int y, int z) {
        return this.block.getMixedBrightnessForBlock(this.blockAccess, x, y, z);
    }

    private static boolean isBoundaryPlane(double[][] vertices, JsonModelDirection facing, int x, int y, int z) {
        int axis = switch (facing) {
            case DOWN, UP -> 1;
            case NORTH, SOUTH -> 2;
            case WEST, EAST -> 0;
        };
        double minimum = getMinimumCoordinate(vertices, axis);
        double maximum = getMaximumCoordinate(vertices, axis);
        if (maximum - minimum > EPSILON) {
            return false;
        }

        return switch (facing) {
            case DOWN -> minimum <= y + EPSILON;
            case UP -> maximum >= y + 1.0D - EPSILON;
            case NORTH -> minimum <= z + EPSILON;
            case SOUTH -> maximum >= z + 1.0D - EPSILON;
            case WEST -> minimum <= x + EPSILON;
            case EAST -> maximum >= x + 1.0D - EPSILON;
        };
    }

    private static int combineBrightness(int first, int second) {
        // MITE packs sky light in bits 20-23 and block light in bits 4-7.
        int blockLight = Math.max(first & BLOCK_LIGHT_MASK, second & BLOCK_LIGHT_MASK);
        int skyLight = Math.max(first & SKY_LIGHT_MASK, second & SKY_LIGHT_MASK);
        return skyLight | blockLight;
    }

    private static double getMinimumCoordinate(double[][] vertices, int axis) {
        double minimum = Double.POSITIVE_INFINITY;
        for (double[] vertex : vertices) {
            minimum = Math.min(minimum, vertex[axis]);
        }
        return minimum;
    }

    private static double getMaximumCoordinate(double[][] vertices, int axis) {
        double maximum = Double.NEGATIVE_INFINITY;
        for (double[] vertex : vertices) {
            maximum = Math.max(maximum, vertex[axis]);
        }
        return maximum;
    }
}
