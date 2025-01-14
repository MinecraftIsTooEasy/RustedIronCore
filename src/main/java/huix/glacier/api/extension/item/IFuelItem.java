package huix.glacier.api.extension.item;

import net.minecraft.Item;
import net.minecraft.ItemBlock;

import java.util.Optional;

/**
 * Block can also implement this.
 */
public interface IFuelItem {
    int getHeatLevel();

    int getBurnTime();

    static Optional<IFuelItem> cast(Item item) {
        if (item instanceof IFuelItem iFuelItem) return Optional.of(iFuelItem);
        if (item instanceof ItemBlock block && block.getBlock() instanceof IFuelItem iFuelItem)
            return Optional.of(iFuelItem);
        return Optional.empty();
    }
}
