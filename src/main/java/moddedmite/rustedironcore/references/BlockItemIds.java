package moddedmite.rustedironcore.references;

import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.util.RegistryUtil;
import net.minecraft.Block;
import net.minecraft.Item;
import net.minecraft.ItemBlock;

public class BlockItemIds {

    public static BlockItemId from(Block block) {
        Identifier id = RegistryUtil.getId(block);
        return BlockItemId.create(id, id);
    }

    public static BlockItemId from(Item item) {
        if (item instanceof ItemBlock itemBlock) {
            Block block = itemBlock.getBlock();
            return from(block);
        }
        throw new AssertionError();
    }
}
