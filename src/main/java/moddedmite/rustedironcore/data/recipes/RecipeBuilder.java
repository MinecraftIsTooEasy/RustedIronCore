package moddedmite.rustedironcore.data.recipes;

import moddedmite.rustedironcore.annotations.NotFinished;
import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.registries.Registries;
import moddedmite.rustedironcore.resources.Identifier;
import moddedmite.rustedironcore.resources.ResourceKey;
import moddedmite.rustedironcore.world.item.ItemInstance;
import moddedmite.rustedironcore.world.item.crafting.CraftingBookCategory;
import moddedmite.rustedironcore.world.item.crafting.CraftingRecipe;
import moddedmite.rustedironcore.world.item.crafting.Recipe;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NotFinished
@NotVanilla
public interface RecipeBuilder {
    Identifier ROOT_RECIPE_ADVANCEMENT = Identifier.withDefaultNamespace("recipes/root");

//	RecipeBuilder unlockedBy(String name, Criterion<?> criterion);

    RecipeBuilder group(@Nullable String group);

    @Nullable ResourceKey<Recipe<?>> defaultId();

    void save(RecipeOutput output, ResourceKey<Recipe<?>> location);

    default void save(final RecipeOutput output) {
        ResourceKey<Recipe<?>> defaultKey = this.defaultId();
        if (defaultKey == null) {
            throw new IllegalStateException("Recipe has no name to derive an id from - save it with an explicit id");
        }

        this.save(output, defaultKey);
    }

    //	default void save(final RecipeOutput output, final String id) {
//		ResourceKey<Recipe<?>> defaultKey = this.defaultId();
//		ResourceKey<Recipe<?>> overriddenKey = ResourceKey.create(Registries.RECIPE, Identifier.parse(id));
//		if (overriddenKey == defaultKey) {
//			throw new IllegalStateException("Recipe " + id + " should remove its 'save' argument as it is equal to default one");
//		}
//
//		this.save(output, overriddenKey);
//	}
//
    static CraftingBookCategory determineCraftingBookCategory(final RecipeCategory category) {
        return switch (category) {
            case BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
            case TOOLS, COMBAT -> CraftingBookCategory.EQUIPMENT;
            case REDSTONE -> CraftingBookCategory.REDSTONE;
            default -> CraftingBookCategory.MISC;
        };
    }

    static Recipe.CommonInfo createCraftingCommonInfo(final boolean showNotification) {
        return new Recipe.CommonInfo(showNotification);
    }

    static CraftingRecipe.CraftingBookInfo createCraftingBookInfo(final RecipeCategory category, final @Nullable String group) {
        return new CraftingRecipe.CraftingBookInfo(determineCraftingBookCategory(category), Objects.requireNonNullElse(group, ""));
    }

    static ResourceKey<Recipe<?>> getDefaultRecipeId(final ItemInstance result) {
        return ResourceKey.create(Registries.RECIPE, result.typeHolder().unwrapKey().orElseThrow().identifier());
    }
}
