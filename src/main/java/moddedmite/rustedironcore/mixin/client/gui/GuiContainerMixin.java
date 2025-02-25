package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.ContainerWorkbench;
import net.minecraft.GuiContainer;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiContainer.class)
public class GuiContainerMixin {
    @WrapOperation(method = "drawItemStackTooltip(Lnet/minecraft/ItemStack;IILnet/minecraft/Slot;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockWorkbench;getToolMaterial(I)Lnet/minecraft/Material;"))
    private Material addWorkbench(int metadata, Operation<Material> original, @Local ContainerWorkbench workbench) {
        if (workbench.world.getBlock(workbench.x, workbench.y, workbench.z) instanceof WorkbenchBlock workbenchBlock) {
            return workbenchBlock.getMaterial();
        } else {
            return original.call(metadata);
        }
    }
}
