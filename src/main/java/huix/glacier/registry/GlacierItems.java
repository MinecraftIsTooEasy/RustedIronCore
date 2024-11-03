package huix.glacier.registry;

import huix.glacier.registry.util.SimpleRegistry;
import huix.glacier.util.Identifier;
import net.minecraft.Item;

public class GlacierItems {
    public static final SimpleRegistry REGISTRY = new SimpleRegistry();

    static {
        for (Item item : Item.itemsList) {
            if (item != null)
                REGISTRY.add(item.itemID, item.getUnlocalizedName(), item);
        }

    }
}
