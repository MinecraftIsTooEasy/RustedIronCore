package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.world.item.ItemStackTemplate;
import net.minecraft.ItemStack;
import net.minecraft.World;

import java.util.List;
import java.util.Optional;

public class ShapedRecipeExtension extends NormalCraftingRecipe {
    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;

    public ShapedRecipeExtension(
            final Recipe.CommonInfo commonInfo, final CraftingRecipe.CraftingBookInfo bookInfo, final ShapedRecipePattern pattern, final ItemStackTemplate result
    ) {
        super(commonInfo, bookInfo);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.createFromOptionals(this.pattern.ingredients());
    }

    @Override
    public boolean matches(CraftingInput input, World level) {
        return this.pattern.matches(input);
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return this.result.create();
    }

    @Override
    public List<Ingredient> getIngredients() {
        return this.pattern.ingredients().stream().filter(Optional::isPresent).map(Optional::get).toList();
    }

    @Override
    public int getRecipeSize() {
        return this.pattern.width() * this.pattern.height();
    }
}
