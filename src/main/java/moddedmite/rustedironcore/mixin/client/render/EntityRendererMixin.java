package moddedmite.rustedironcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.model.JsonModelDisplayContext;
import moddedmite.rustedironcore.api.model.JsonModelRenderContext;
import net.minecraft.EntityRenderer;
import net.minecraft.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "updateCameraAndRender(F)V", at = @At("HEAD"))
    private void beginModelFrame(float partialTick, CallbackInfo ci) {
        JsonModelRenderContext.beginFrame(partialTick);
    }

    @WrapOperation(method = "renderHand(FI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemRenderer;renderItemInFirstPerson(F)V"))
    private void renderWithFirstPersonContext(ItemRenderer renderer, float partialTicks, Operation<Void> original) {
        JsonModelRenderContext.push(JsonModelDisplayContext.FIRST_PERSON_RIGHT_HAND);
        try {
            original.call(renderer, partialTicks);
        } finally {
            JsonModelRenderContext.pop();
        }
    }
}
