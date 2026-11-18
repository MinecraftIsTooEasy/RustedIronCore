package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.IMerchant;
import net.minecraft.MerchantRecipe;
import net.minecraft.SlotMerchantResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlotMerchantResult.class)
public class SlotMerchantResultMixin {

    @WrapOperation(method = "onPickupFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/IMerchant;useRecipe(Lnet/minecraft/MerchantRecipe;)V"))
    private void trackTrade(IMerchant merchant, MerchantRecipe recipe, Operation<Void> original, @Local(argsOnly = true) EntityPlayer player) {
        original.call(merchant, recipe);
        CriteriaTriggers.traded(player, merchant instanceof Entity entity ? entity : null, recipe.getItemToSell());
    }
}
