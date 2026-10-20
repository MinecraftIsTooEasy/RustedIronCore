package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import net.minecraft.Item;

public interface RicItemImpl {
    Holder.Reference<Item> ric$builtInRegistryHolder();

    @SuppressWarnings({"Convert2MethodRef", "RedundantCast"})
    static RicItem cast(Item item) {
        RicItemImpl instance = (RicItemImpl) (Object) item;
        return () -> instance.ric$builtInRegistryHolder();
    }
}
