package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.IToolMaterial;
import net.minecraft.ItemTool;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ItemTool.class )
public class ItemToolMixin {

    @Shadow
    private Material effective_material;
    @Inject(method = "getMaterialHarvestEfficiency", at = @At("HEAD"), cancellable = true)
    public void injectNetheriteHarvestEfficiency(CallbackInfoReturnable<Float> cir) {
        if (this.effective_material instanceof IToolMaterial toolMaterial) {
            cir.setReturnValue(toolMaterial.getHarvestEfficiency());
        }
    }
}
