package huix.glacier.registry;

import huix.glacier.registry.util.BiDefaultedRegistry;
import huix.glacier.registry.util.SimpleRegistry;
import huix.glacier.util.Identifier;
import net.minecraft.Block;
import net.minecraft.Item;

public class GlacierBlocks {
    public static final SimpleRegistry REGISTRY = new BiDefaultedRegistry("air");

    static {
        for (Block block : Block.blocksList) {
            if (block != null)
                REGISTRY.add(block.blockID, block.getUnlocalizedName(), block);
        }
    }
}
