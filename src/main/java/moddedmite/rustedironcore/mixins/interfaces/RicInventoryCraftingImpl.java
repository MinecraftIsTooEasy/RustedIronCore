package moddedmite.rustedironcore.mixins.interfaces;

import net.minecraft.InventoryCrafting;

public interface RicInventoryCraftingImpl {
    int ric$getWidth();

    int ric$getHeight();

    @SuppressWarnings("RedundantCast")
    static RicInventoryCrafting cast(InventoryCrafting inventory) {
        RicInventoryCraftingImpl instance = (RicInventoryCraftingImpl) (Object) inventory;
        return new RicInventoryCrafting() {
            @Override
            public int getWidth() {
                return instance.ric$getWidth();
            }

            @Override
            public int getHeight() {
                return instance.ric$getHeight();
            }
        };
    }
}
