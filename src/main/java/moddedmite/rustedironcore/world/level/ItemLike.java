package moddedmite.rustedironcore.world.level;

import net.minecraft.Block;
import net.minecraft.Item;

public interface ItemLike {
    Item asItem();

    static ItemLike cast(Object o) {
        if (o instanceof Item || o instanceof Block) {
            return () -> {
                if (o instanceof Item item) return item;
                if (o instanceof Block block) return Item.getItem(block);
                throw new AssertionError();
            };
        }
        throw new AssertionError();
    }
}
