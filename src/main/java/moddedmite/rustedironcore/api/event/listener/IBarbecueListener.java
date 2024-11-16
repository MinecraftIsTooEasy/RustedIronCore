package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.ItemStack;

public interface IBarbecueListener {
    default ItemStack getCookResult(ItemStack input) {
        return null;
    }

    // if not, the item will die in fire
    default boolean isCookResult(ItemStack itemStack) {
        return false;
    }
}
