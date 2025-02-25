package moddedmite.rustedironcore.mixin.item;

import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.Block;
import net.minecraft.ItemBlock;
import net.minecraft.ItemStack;
import net.minecraft.Translator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlock.class)
public abstract class ItemBlockMixin {
    @Shadow
    public abstract Block getBlock();

    @Inject(method = "getItemDisplayName", at = @At("HEAD"), cancellable = true)
    private void addWorkbenchTranslation(ItemStack item_stack, CallbackInfoReturnable<String> cir) {
        if (item_stack == null) return;
        Block block = this.getBlock();
        if (block instanceof WorkbenchBlock workbenchBlock) {
            cir.setReturnValue(Translator.get("tile.toolbench." + workbenchBlock.getMaterial().name + ".name"));
        }
    }
}
