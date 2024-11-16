package moddedmite.rustedironcore.mixin.other.item;

import huix.glacier.api.extension.item.IFuelItem;
import huix.glacier.api.extension.material.IRepairableMaterial;
import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.ItemRegistryRemapper;
import moddedmite.rustedironcore.property.ItemProperties;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow
    public abstract Material getMaterialForRepairs();

    @Inject(method = "getRepairItem", at = @At("HEAD"), cancellable = true)
    private void addRepairItem(CallbackInfoReturnable<Item> cir) {
        if (this.getMaterialForRepairs() instanceof IRepairableMaterial repairableMaterial) {
            cir.setReturnValue(repairableMaterial.getRepairItem());
            return;
        }
        Material material_for_repairs = this.getMaterialForRepairs();
        Item item = MaterialProperties.RepairItem.get(material_for_repairs);
        if (item != null) {
            cir.setReturnValue(item);
        }
    }

    @Inject(method = "getHeatLevel", at = @At("HEAD"), cancellable = true)
    private void injectHeatLevel(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        if (item_stack == null) return;
        Item item = item_stack.getItem();
        if (item instanceof IFuelItem iFuelItem) {
            cir.setReturnValue(iFuelItem.getHeatLevel());
        }
        ItemProperties.HeatLevel.getOptional(item).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getBurnTime", at = @At("HEAD"), cancellable = true)
    private void injectBurnTime(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        if (item_stack == null) return;
        Item item = item_stack.getItem();
        if (item instanceof IFuelItem iFuelItem) {
            cir.setReturnValue(iFuelItem.getBurnTime());
        }
        ItemProperties.BurnTime.getOptional(item_stack.getItem()).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(ItemRegistryRemapper::new);
    }

}
