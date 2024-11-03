package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.IArrowMaterial;
import huix.glacier.util.GlacierHelper;
import net.minecraft.ItemArrow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin( ItemArrow.class )
public class ItemArrowMixin {

    @Shadow
    @Final
    public Material arrowhead_material;

    @Inject(method = "getChanceOfRecovery",at = @At("HEAD"), cancellable = true)
    private void injectGetChanceOfRecovery(CallbackInfoReturnable<Float> cir){
        if (this.arrowhead_material instanceof IArrowMaterial arrowMaterial) {
            cir.setReturnValue(arrowMaterial.getChanceOfRecovery());
        }
    }


    @Overwrite
    public static int getArrowIndex(Material arrowhead_material) {
        for(int i = 0; i < GlacierHelper.arrow_material_types.size(); ++i) {
            if (GlacierHelper.arrow_material_types.get(i) == arrowhead_material) {
                return i;
            }
        }

        return -1;
    }
}
