package moddedmite.rustedironcore.data.recipes;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.world.item.crafting.CraftingRecipe;

@NotFinished
@NotVanilla
public interface RecipeOutput {
    void accept(CraftingRecipe recipe);
}
