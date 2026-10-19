package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import net.minecraft.Item;

public interface RicItemImpl extends RicItem {
    Holder.Reference<Item> ric$builtInRegistryHolder();

    @Override
    default Holder.Reference<Item> builtInRegistryHolder() {
        return this.ric$builtInRegistryHolder();
    }
}
