package moddedmite.rustedironcore.mixin.other.entity.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.ClientPlayer;
import net.minecraft.Container;
import net.minecraft.ContainerWorkbench;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayer.class)
public class ClientPlayerMixin {
    @WrapOperation(method = "getBenchAndToolsModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockWorkbench;getToolMaterial(I)Lnet/minecraft/Material;"))
    private Material redirectWorkBench(int metadata, Operation<Material> original, @Local ContainerWorkbench workbench) {
        if (workbench.world.getBlock(workbench.x, workbench.y, workbench.z) instanceof WorkbenchBlock workbenchBlock) {
            return workbenchBlock.getMaterial();
        } else {
            return original.call(metadata);
        }
    }

    @Inject(method = "getBenchAndToolsModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;)V"), cancellable = true)
    private void injectModifyWorkbenchModifier(Container container, CallbackInfoReturnable<Float> cir, @Local ContainerWorkbench containerWorkbench) {
        if (containerWorkbench.world.getBlock(containerWorkbench.x, containerWorkbench.y, containerWorkbench.z) instanceof WorkbenchBlock workbenchBlock) {
            cir.setReturnValue(workbenchBlock.getSpeedModifier());
        }
    }
}
