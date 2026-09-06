package moddedmite.rustedironcore.api.block;

import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import net.minecraft.World;

/**
 * Supplies a coordinate-dependent block-light emission value for blocks whose
 * light level is encoded in metadata or other world state.
 */
public interface DynamicLightBlock {

    int getLightEmission(IBlockAccess access, int x, int y, int z);

    static int resolve(IBlockAccess access, int x, int y, int z, int fallback) {
        Block block = access.getBlock(x, y, z);
        if (!(block instanceof DynamicLightBlock dynamicLightBlock)) {
            return fallback;
        }
        return Math.max(0, Math.min(15, dynamicLightBlock.getLightEmission(access, x, y, z)));
    }

    static void updateLight(World world, int x, int y, int z) {
        world.propagateBlocklight(x, y, z, false, world.getChunkFromBlockCoords(x, z));
    }
}
