package moddedmite.rustedironcore.internal.event;

import moddedmite.rustedironcore.data.recipes.RecipeCategory;
import moddedmite.rustedironcore.data.recipes.RecipeProvider;
import moddedmite.rustedironcore.tags.ItemTags;
import net.minecraft.Item;

public class InternalRecipeProvider extends RecipeProvider {
    public InternalRecipeProvider() {
        super();
    }

    @Override
    public void buildRecipes() {
        this.shaped(RecipeCategory.MISC, Item.stick, 4)
                .define('#', ItemTags.FISHES)
                .pattern("#")
                .pattern("#")
                .group("sticks")
//                .unlockedBy("has_planks", this.has(ItemTags.PLANKS))
                .save(this.output);
    }
}
