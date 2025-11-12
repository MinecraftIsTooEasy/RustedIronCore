package moddedmite.rustedironcore.mixin.item;

import huix.glacier.api.extension.item.IFuelItem;
import huix.glacier.api.extension.material.IRepairableMaterial;
import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.ItemRegistryRemapper;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.property.ItemProperties;
import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow
    public abstract Material getMaterialForRepairs();

    @Inject(method = "getRepairItem", at = @At("HEAD"), cancellable = true)
    private void addRepairItem(CallbackInfoReturnable<Item> cir) {
        Material material_for_repairs = this.getMaterialForRepairs();
        if (material_for_repairs instanceof IRepairableMaterial repairableMaterial) {
            cir.setReturnValue(repairableMaterial.getRepairItem());
            return;
        }
        Item item = MaterialProperties.RepairItem.get(material_for_repairs);
        if (item != null) {
            cir.setReturnValue(item);
        }
    }

    @Inject(method = "getHeatLevel", at = @At("HEAD"), cancellable = true)
    private void injectHeatLevel(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        if (item_stack == null) return;
        Item item = item_stack.getItem();
        IFuelItem.cast(item).ifPresent(x -> cir.setReturnValue(x.getHeatLevel()));
        ItemProperties.HeatLevel.getOptional(item).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "getBurnTime", at = @At("HEAD"), cancellable = true)
    private void injectBurnTime(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        if (item_stack == null) return;
        Item item = item_stack.getItem();
        IFuelItem.cast(item).ifPresent(x -> cir.setReturnValue(x.getBurnTime()));
        ItemProperties.BurnTime.getOptional(item_stack.getItem()).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(ItemRegistryRemapper::new);
    }

    @Inject(method = "addInformation(Lnet/minecraft/ItemStack;Lnet/minecraft/EntityPlayer;Ljava/util/List;ZLnet/minecraft/Slot;)V", at = @At(value = "RETURN", ordinal = 1))
    private void onTooltipFood(ItemStack stack, EntityPlayer player, List<String> info, boolean detailed, Slot slot, CallbackInfo ci) {
        Handlers.Tooltip.onTooltipBody(stack, info, player, detailed, slot);
    }
}
