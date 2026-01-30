package moddedmite.rustedironcore.mixin.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SmeltingHandler;
import net.minecraft.FurnaceRecipes;
import net.minecraft.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceRecipes.class)
public class FurnaceRecipesMixin {
    @Inject(method = "getSmeltingResult", at = @At(value = "FIELD", target = "Lnet/minecraft/ItemStack;itemID:I", opcode = Opcodes.GETFIELD, ordinal = 0), cancellable = true)
    private void addSpecialRecipes(ItemStack input_item_stack, int heat_level, CallbackInfoReturnable<ItemStack> cir) {
        if (heat_level == -1) heat_level = 32;
        // this 32 is dummy, only to enable putting into the furnace
        Handlers.Smelting.match(input_item_stack, heat_level)
                .map(SmeltingHandler.SmeltingResult::result)
                .ifPresent(cir::setReturnValue);
    }
}
