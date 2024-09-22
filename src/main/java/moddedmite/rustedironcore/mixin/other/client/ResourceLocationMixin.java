package moddedmite.rustedironcore.mixin.other.client;

import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourceLocation.class)
public class ResourceLocationMixin {
    @Inject(method = "exists", at = @At("RETURN"), cancellable = true)
    public void removeWarning(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }
}
