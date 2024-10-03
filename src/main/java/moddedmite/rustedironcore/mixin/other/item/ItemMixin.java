package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.property.ItemProperties;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow
    public abstract Material getMaterialForRepairs();

    @Inject(method = "getRepairItem", at = @At("HEAD"), cancellable = true)
    private void addRepairItem(CallbackInfoReturnable<Item> cir) {
        Material material_for_repairs = this.getMaterialForRepairs();
        Item item = MaterialProperties.RepairItem.get(material_for_repairs);
        if (item != null) {
            cir.setReturnValue(item);
        }
    }

    @Inject(method = "getHeatLevel", at = @At("HEAD"), cancellable = true)
    private void injectHeatLevel(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        ItemProperties.HeatLevel.getOptional(item_stack.getItem()).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getBurnTime", at = @At("HEAD"), cancellable = true)
    private void injectBurnTime(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        ItemProperties.BurnTime.getOptional(item_stack.getItem()).ifPresent(cir::setReturnValue);
    }

}
