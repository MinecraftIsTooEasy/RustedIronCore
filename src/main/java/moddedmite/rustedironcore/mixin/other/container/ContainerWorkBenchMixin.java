package moddedmite.rustedironcore.mixin.other.container;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.Block;
import net.minecraft.ContainerWorkbench;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ContainerWorkbench.class)
public class ContainerWorkBenchMixin {
    @ModifyExpressionValue(method = "canInteractWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;getBlockId(III)I"))
    private int canInteract(int original) {
        if (Block.getBlock(original) instanceof WorkbenchBlock) return Block.workbench.blockID;
        return original;
    }
}
