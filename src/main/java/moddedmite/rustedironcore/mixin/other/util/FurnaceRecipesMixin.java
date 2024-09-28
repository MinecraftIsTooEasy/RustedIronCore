package moddedmite.rustedironcore.mixin.other.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SmeltingHandler;
import net.minecraft.FurnaceRecipes;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceRecipes.class)
public class FurnaceRecipesMixin {
    @Inject(method = "getSmeltingResult", at = @At(value = "FIELD", target = "Lnet/minecraft/Block;sand:Lnet/minecraft/BlockSand;"), cancellable = true)
    private void addSpecialRecipes(ItemStack input_item_stack, int heat_level, CallbackInfoReturnable<ItemStack> cir) {
        Handlers.Smelting.match(input_item_stack, heat_level).map(SmeltingHandler.SmeltingResult::result).ifPresent(cir::setReturnValue);
    }
}
