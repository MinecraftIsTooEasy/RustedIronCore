package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.world.item.ItemInstance;
import net.minecraft.Item;

public interface ItemInstanceImpl extends ItemInstance {
    Holder<Item> ric$typeHolder();

    int ric$count();

    int ric$getMaxStackSize();

    @Override
    default Holder<Item> typeHolder() {
        return this.ric$typeHolder();
    }

    @Override
    default int count() {
        return this.ric$count();
    }

    @Override
    default int getMaxStackSize() {
        return this.ric$getMaxStackSize();
    }
}
