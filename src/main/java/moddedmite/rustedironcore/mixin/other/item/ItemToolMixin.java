package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.property.MaterialProperties;
import net.minecraft.Item;
import net.minecraft.ItemTool;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemTool.class)
public class ItemToolMixin extends Item {
    @Shadow
    private Material effective_material;

    @Inject(method = "getMaterialHarvestEfficiency", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;)V"), cancellable = true)
    private void addMaterial(CallbackInfoReturnable<Float> cir) {
        MaterialProperties.HarvestEfficiency.getOptional(this.effective_material).ifPresent(cir::setReturnValue);
    }
}
