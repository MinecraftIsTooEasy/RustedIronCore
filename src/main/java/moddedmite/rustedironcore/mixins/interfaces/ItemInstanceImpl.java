package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.world.item.ItemInstance;
import net.minecraft.Item;
import net.minecraft.ItemStack;

public interface ItemInstanceImpl {
    Holder<Item> ric$typeHolder();

    int ric$count();

    int ric$getMaxStackSize();

    @SuppressWarnings("DataFlowIssue")
    static ItemInstance cast(ItemStack stack) {
        ItemInstanceImpl instance = (ItemInstanceImpl) (Object) stack;
        return new ItemInstance() {
            @Override
            public Holder<Item> typeHolder() {
                return instance.ric$typeHolder();
            }

            @Override
            public int count() {
                return instance.ric$count();
            }

            @Override
            public int getMaxStackSize() {
                return instance.ric$getMaxStackSize();
            }
        };
    }
}
