package moddedmite.rustedironcore.mixin.item;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onTooltip(EntityPlayer par1EntityPlayer, boolean par2, Slot slot, CallbackInfoReturnable<List<String>> cir) {
        Handlers.Tooltip.onTooltip(cir.getReturnValue(), par1EntityPlayer, par2, slot);
    }
}
