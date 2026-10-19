package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import net.minecraft.Item;

public interface RicItem {
    Holder.Reference<Item> builtInRegistryHolder();

    static RicItem cast(Item item) {
        return (RicItem) (Object) item;
    }
}
