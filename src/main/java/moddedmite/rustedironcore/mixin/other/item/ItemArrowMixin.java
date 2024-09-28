package moddedmite.rustedironcore.mixin.other.item;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ItemArrow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemArrow.class)
public class ItemArrowMixin {
    @Shadow
    @Final
    @Mutable
    public static Material[] material_types;

    @Shadow
    @Final
    public Material arrowhead_material;

    @Inject(method = "<clinit>()V", at = @At("RETURN"))
    private static void addArrowMaterials(CallbackInfo callback) {
        Handlers.ArrowRegister.onRegister();
        Material[] original = material_types;
        Material[] arrowMaterials = Handlers.ArrowRegister.keySet().toArray(Material[]::new);
        Material[] expanded = new Material[original.length + arrowMaterials.length];
        System.arraycopy(original, 0, expanded, 0, original.length);
        System.arraycopy(arrowMaterials, 0, expanded, original.length, arrowMaterials.length);
        material_types = expanded;
    }

    @Inject(method = "getChanceOfRecovery", at = @At("HEAD"), cancellable = true)
    private void addArrows(CallbackInfoReturnable<Float> cir) {
        Handlers.ArrowRegister.match(this.arrowhead_material).ifPresent(cir::setReturnValue);
    }
}
