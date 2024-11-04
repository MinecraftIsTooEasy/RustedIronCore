package huix.glacier.api.extension.item;

import net.minecraft.ItemStack;

public interface IRetainableItem {
    ItemStack getItemStackAfterCrafting(ItemStack original);
}
