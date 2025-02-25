package moddedmite.rustedironcore.mixin.dimension.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.DimensionHandler;
import net.minecraft.WorldProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldProvider.class)
public class WorldProviderMixin {
    @Mutable
    @Shadow
    @Final
    public boolean hasNoSky;

    @Shadow
    @Final
    public int dimensionId;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;)V"))
    private void mute(String text, Operation<Void> original) {
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(int dimension_id, String name, CallbackInfo ci) {
        DimensionHandler handler = Handlers.Dimension;
        handler.getDimensionContext(dimensionId).ifPresent(context -> {
            this.hasNoSky = !context.hasSkyLight();
        });
    }

    @Inject(method = "getProviderForDimension", at = @At("HEAD"), cancellable = true)
    private static void onGetProviderForDimension(int dimensionId, CallbackInfoReturnable<WorldProvider> cir) {
        DimensionHandler handler = Handlers.Dimension;
        handler.getDimensionContext(dimensionId).ifPresent(x -> cir.setReturnValue(x.worldProviderFactory().get()));
    }
}
