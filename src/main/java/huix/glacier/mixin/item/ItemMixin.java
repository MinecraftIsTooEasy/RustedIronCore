package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.IRepairableMaterial;
import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.BlockRegistryRemapper;
import huix.glacier.api.registry.sync.remappers.ItemRegistryRemapper;
import net.minecraft.Item;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( Item.class )
public class ItemMixin {

    @Inject(method = "getRepairItem", at = @At("HEAD"), cancellable = true)
    public void injectMaterialRepair(CallbackInfoReturnable<Item> cir) {
        if (this.getMaterialForRepairs() instanceof IRepairableMaterial repairableMaterial)
            cir.setReturnValue(repairableMaterial.getRepairItem());
    }

    @Shadow
    public Material getMaterialForRepairs() {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(ItemRegistryRemapper::new);
    }

}
