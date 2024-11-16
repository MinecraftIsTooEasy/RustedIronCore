package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;

public interface IAchievementListener {
    default void onItemCrafted(EntityPlayer player, ItemStack itemStack) {
    }

    default void onItemPickUp(EntityPlayer player, ItemStack itemStack) {
    }

    default void onItemSmelt(EntityPlayer player, ItemStack itemStack) {
    }

    default void onDimensionTravel(EntityPlayer player, int currentDimension, int destinationDimension) {
    }
}
