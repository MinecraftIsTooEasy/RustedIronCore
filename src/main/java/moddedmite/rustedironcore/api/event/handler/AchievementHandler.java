package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IAchievementListener;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;

public class AchievementHandler extends AbstractHandler<IAchievementListener> implements IAchievementListener {
    @Override
    public void onItemCrafted(EntityPlayer player, ItemStack itemStack) {
        this.listeners.forEach(x -> x.onItemCrafted(player, itemStack));
    }

    @Override
    public void onItemPickUp(EntityPlayer player, ItemStack itemStack) {
        this.listeners.forEach(x -> x.onItemPickUp(player, itemStack));
    }

    @Override
    public void onItemSmelt(EntityPlayer player, ItemStack itemStack) {
        this.listeners.forEach(x -> x.onItemSmelt(player, itemStack));
    }

    @Override
    public void onDimensionTravel(EntityPlayer player, int currentDimension, int destinationDimension) {
        this.listeners.forEach(x -> x.onDimensionTravel(player, currentDimension, destinationDimension));
    }
}
