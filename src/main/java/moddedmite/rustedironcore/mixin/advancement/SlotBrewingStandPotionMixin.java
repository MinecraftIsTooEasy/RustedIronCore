package moddedmite.rustedironcore.mixin.advancement;

import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.EntityPlayer;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.SlotBrewingStandPotion")
public class SlotBrewingStandPotionMixin {

    @Inject(method = "onPickupFromSlot", at = @At("HEAD"))
    private void trackPotion(EntityPlayer player, ItemStack stack, CallbackInfo callback) {
        if (stack.getItem() == Item.potion && stack.getItemSubtype() > 0) CriteriaTriggers.brewedPotion(player, stack);
    }
}
