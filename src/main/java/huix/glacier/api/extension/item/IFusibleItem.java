package huix.glacier.api.extension.item;

import net.minecraft.Item;
import net.minecraft.ItemBlock;

import java.util.Optional;

/**
 * Block can also implement this.
 */
public interface IFusibleItem {
    int getHeatLevelRequired();

    static Optional<IFusibleItem> cast(Item item) {
        if (item instanceof IFusibleItem iFusibleItem) return Optional.of(iFusibleItem);
        if (item instanceof ItemBlock block && block.getBlock() instanceof IFusibleItem iFusibleItem)
            return Optional.of(iFusibleItem);
        return Optional.empty();
    }
}
