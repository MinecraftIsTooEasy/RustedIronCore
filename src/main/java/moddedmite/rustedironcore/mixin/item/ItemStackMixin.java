package moddedmite.rustedironcore.mixin.item;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void onTooltipTail(EntityPlayer player, boolean detailed, Slot slot, CallbackInfoReturnable<List<String>> cir) {
        Handlers.Tooltip.onTooltipTail((ItemStack) (Object) this, cir.getReturnValue(), player, detailed, slot);
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;add(Ljava/lang/Object;)Z", ordinal = 0, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onTooltipHead(EntityPlayer player, boolean detailed, Slot slot, CallbackInfoReturnable<List<String>> cir, ArrayList<String> tooltip) {
        Handlers.Tooltip.onTooltipHead((ItemStack) (Object) this, tooltip, player, detailed, slot);
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onTooltipAttributeModifier(EntityPlayer player, boolean detailed, Slot slot, CallbackInfoReturnable<List<String>> cir, ArrayList<String> tooltip) {
        Handlers.Tooltip.onTooltipWaist((ItemStack) (Object) this, tooltip, player, detailed, slot);
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/Item;addInformation(Lnet/minecraft/ItemStack;Lnet/minecraft/EntityPlayer;Ljava/util/List;ZLnet/minecraft/Slot;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onTooltipEnchantment(EntityPlayer player, boolean detailed, Slot slot, CallbackInfoReturnable<List<String>> cir, ArrayList<String> tooltip) {
        Handlers.Tooltip.onTooltipNeck((ItemStack) (Object) this, tooltip, player, detailed, slot);
    }
}
