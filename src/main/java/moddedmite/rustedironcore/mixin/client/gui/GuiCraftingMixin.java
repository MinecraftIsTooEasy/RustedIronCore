package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiCrafting.class)
public abstract class GuiCraftingMixin extends InventoryEffectRenderer {
    public GuiCraftingMixin(Container par1Container) {
        super(par1Container);
    }

    @WrapOperation(method = "drawGuiContainerForegroundLayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockWorkbench;getToolMaterial(I)Lnet/minecraft/Material;"))
    private Material addWorkbenchName(int metadata, Operation<Material> original) {
        ContainerWorkbench workbench = (ContainerWorkbench) this.inventorySlots;
        if (workbench.world.getBlock(workbench.x, workbench.y, workbench.z) instanceof WorkbenchBlock workbenchBlock) {
            return workbenchBlock.getMaterial();
        } else {
            return original.call(metadata);
        }
    }
}
