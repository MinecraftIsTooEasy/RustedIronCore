package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.world.item.ItemStackTemplate;
import net.minecraft.ItemStack;
import net.minecraft.World;

import java.util.List;

public class ShapelessRecipeExtension extends NormalCraftingRecipe {
    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients;

    public ShapelessRecipeExtension(
            final Recipe.CommonInfo commonInfo, final CraftingRecipe.CraftingBookInfo bookInfo, final ItemStackTemplate result, final List<Ingredient> ingredients
    ) {
        super(commonInfo, bookInfo);
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.create(this.ingredients);
    }

    @Override
    public boolean matches(final CraftingInput input, final World level) {
        if (input.ingredientCount() != this.ingredients.size()) {
            return false;
        } else {
            return input.size() == 1 && this.ingredients.size() == 1 ?
                    this.ingredients.get(0).test(input.getItem(0)) :
                    input.stackedContents().canCraft(this, null);
        }
    }

    @Override
    public ItemStack assemble(final CraftingInput input) {
        return this.result.create();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }
}
