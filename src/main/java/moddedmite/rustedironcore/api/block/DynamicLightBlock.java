package moddedmite.rustedironcore.api.block;

import net.minecraft.Block;
import net.minecraft.IBlockAccess;

/**
 * Supplies a coordinate-dependent block-light emission value for blocks whose
 * light level is encoded in metadata or other world state. RIC automatically
 * propagates block light when the block or its metadata changes.
 */
public interface DynamicLightBlock {

    int getLightEmission(IBlockAccess access, int x, int y, int z);

    static int resolve(IBlockAccess access, int x, int y, int z, int staticEmission) {
        Block block = access.getBlock(x, y, z);
        if (!(block instanceof DynamicLightBlock dynamicLightBlock)) {
            return staticEmission;
        }
        return Math.max(0, Math.min(15, dynamicLightBlock.getLightEmission(access, x, y, z)));
    }

}
