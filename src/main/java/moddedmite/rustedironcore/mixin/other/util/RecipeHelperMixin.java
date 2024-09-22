package moddedmite.rustedironcore.mixin.other.util;

import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeHelper.class)
public class RecipeHelperMixin {
    @Inject(method = "addRecipe", at = @At("RETURN"))
    private static void modifyRecipe(IRecipe recipe, boolean include_in_lowest_crafting_difficulty_determination, CallbackInfo ci) {
        ItemStack recipe_output = recipe.getRecipeOutput();
        if (recipe_output == null) return;

        Item output_item = recipe_output.getItem();

        if (output_item instanceof ItemBlock itemBlock && itemBlock.getBlock() instanceof WorkbenchBlock workbenchBlock) {
            recipe.setMaterialToCheckToolBenchHardnessAgainst(workbenchBlock.getMaterialToCheckToolBenchHardnessAgainst());
        }
    }
}
