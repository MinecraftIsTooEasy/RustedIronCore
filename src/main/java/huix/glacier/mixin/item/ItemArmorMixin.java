package huix.glacier.mixin.item;

import huix.glacier.api.extension.material.IArmorMaterial;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin( ItemArmor.class )
public class ItemArmorMixin {
    @Shadow
    protected Material effective_material;

    @Inject(method = "getMaterialProtection", at = @At("HEAD"), cancellable = true)
    public void injectNetheriteProtections(CallbackInfoReturnable<Integer> cir) {
        if (this.effective_material instanceof IArmorMaterial armorMaterial) {
            cir.setReturnValue(armorMaterial.getProtection());
        }
    }
}
