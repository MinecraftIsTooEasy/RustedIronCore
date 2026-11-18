package moddedmite.rustedironcore.mixin.client.render;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.block.Bed;
import moddedmite.rustedironcore.api.model.JsonModelDisplayContext;
import moddedmite.rustedironcore.api.model.JsonModelRenderContext;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import net.minecraft.Block;
import net.minecraft.EntityRenderer;
import net.minecraft.ItemRenderer;
import net.minecraft.WorldClient;
import net.minecraft.WorldProvider;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @WrapOperation(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;getBlockId(III)I"))
    private int recognizeCameraBed(WorldClient world, int x, int y, int z, Operation<Integer> original) {
        int id = original.call(world, x, y, z);
        return Block.blocksList[id] instanceof Bed bed && bed.isBed(world, x, y, z) ? Block.bed.blockID : id;
    }

    @WrapOperation(method = "orientCamera(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldClient;getBlockMetadata(III)I"))
    private int resolveCameraBedDirection(WorldClient world, int x, int y, int z, Operation<Integer> original) {
        int metadata = original.call(world, x, y, z);
        Bed bed = Bed.getBed(world, x, y, z);
        return bed == null ? metadata : (metadata & ~3) | bed.getBedDirection(world, x, y, z);
    }

    @Inject(method = "updateCameraAndRender(F)V", at = @At("HEAD"))
    private void beginModelFrame(float partialTick, CallbackInfo ci) {
        JsonModelRenderContext.beginFrame(partialTick);
    }

    @ModifyExpressionValue(method = "updateRenderer", at = @At(value = "FIELD", target = "Lnet/minecraft/BossStatus;field_82825_d:Z", opcode = Opcodes.GETSTATIC))
    private boolean darkenForBossBars(boolean legacyDarkening) {
        return legacyDarkening || ClientHud.getBosses().shouldDarkenScreen();
    }

    @WrapOperation(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/WorldProvider;doesXZShowFog(III)Z"))
    private boolean fogForBossBars(WorldProvider provider, int x, int y, int z, Operation<Boolean> original) {
        return original.call(provider, x, y, z) || ClientHud.getBosses().shouldCreateWorldFog();
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
