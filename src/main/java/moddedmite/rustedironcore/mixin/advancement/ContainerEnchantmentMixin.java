package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.ContainerEnchantment;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Item;
import net.minecraft.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerEnchantment.class)
public class ContainerEnchantmentMixin {

    @Shadow public int[] enchantLevels;

    @Inject(method = "enchantItem", at = @At("HEAD"))
    private void captureEnchantmentLevel(EntityPlayer player, int slot, CallbackInfoReturnable<Boolean> callback, @Share("ric$enchantmentLevel") LocalIntRef levels) {
        levels.set(slot >= 0 && slot < enchantLevels.length ? enchantLevels[slot] : 0);
    }

    @Inject(method = "enchantItem", at = @At("RETURN"))
    private void trackEnchantment(EntityPlayer player, int slot, CallbackInfoReturnable<Boolean> callback, @Share("ric$enchantmentLevel") LocalIntRef levels) {
        if (!(player instanceof ServerPlayer) || !callback.getReturnValue()) return;
        ItemStack stack = ((ContainerEnchantment) (Object) this).getSlot(0).getStack();
        if (stack != null && (stack.isItemEnchanted() || stack.getItem() == Item.enchantedBook)) CriteriaTriggers.enchantedItem(player, stack, levels.get());
    }
}
