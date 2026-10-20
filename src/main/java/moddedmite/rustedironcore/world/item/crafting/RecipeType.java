package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.core.Registry;
import moddedmite.rustedironcore.core.registries.BuiltInRegistries;
import moddedmite.rustedironcore.resources.Identifier;

public interface RecipeType<T extends Recipe<?>> {
	RecipeType<CraftingRecipe> CRAFTING = register("crafting");
//	RecipeType<SmeltingRecipe> SMELTING = register("smelting");
//	RecipeType<BlastingRecipe> BLASTING = register("blasting");
//	RecipeType<SmokingRecipe> SMOKING = register("smoking");
//	RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");
//	RecipeType<StonecutterRecipe> STONECUTTING = register("stonecutting");
//	RecipeType<SmithingRecipe> SMITHING = register("smithing");
//	RecipeType<BrewingRecipe> BREWING = register("brewing");

	static <T extends Recipe<?>> RecipeType<T> register(final String name) {
		return Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.withDefaultNamespace(name), new RecipeType<T>() {
			@Override
			public String toString() {
				return name;
			}
		});
	}
}
