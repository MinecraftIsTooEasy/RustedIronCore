package moddedmite.rustedironcore.util;

import moddedmite.rustedironcore.api.block.WorkbenchBlock;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.mixin.world.material.IMaterialMixin;
import moddedmite.rustedironcore.world.item.crafting.CraftingRecipe;
import moddedmite.rustedironcore.world.item.crafting.Ingredient;
import net.minecraft.*;

import java.util.List;

public class RecipeHelper {
    @SuppressWarnings("deprecation")
    public static void buildRecipe(CraftingRecipe recipe) {
        List<Ingredient> ingredients = recipe.getIngredients();
        ItemStack recipe_output = recipe.getRecipeOutput();
        Item output_item = recipe_output.getItem();
        output_item.setAsCraftingProduct();
        output_item.recipes[output_item.num_recipes++] = recipe;
        float difficulty = 0.0F;

        for (Ingredient ingredient : ingredients) {
            for (Holder<Item> x : ingredient.items().toList()) {
                Item item = x.value();
                item.setAsComponentOfCraftingProduct(recipe_output);

                float component_difficulty = item.getCraftingDifficultyAsComponent(null);
                if (component_difficulty < 0.0F) {
                    float lowest_crafting_difficulty_to_produce = item.getLowestCraftingDifficultyToProduce();
                    if (lowest_crafting_difficulty_to_produce != Float.MAX_VALUE) {
                        item.setCraftingDifficultyAsComponent(lowest_crafting_difficulty_to_produce);
                        component_difficulty = lowest_crafting_difficulty_to_produce;
                    } else if (item.hasMaterial(Material.rusted_iron)) {
                        component_difficulty = handleRustedIron(item, component_difficulty);
                    }
                }

                if (component_difficulty < 0.0F) {
                    Minecraft.setErrorMessage(
                            "Warning: recipe for "
                                    + recipe_output.getDisplayName()
                                    + ", component crafting difficulty not set: "
                                    + item.getItemDisplayName()
                                    + " ["
                                    + (item.itemID - 256)
                                    + "]"
                    );
                } else {
                    difficulty += component_difficulty;
                }
            }
        }

        if (difficulty < 0.0F) {
            Minecraft.setErrorMessage("addRecipe: recipe output cannot have a crafting difficulty < 0.0F");
        }

        recipe.setDifficulty(difficulty);
        if (recipe_output.stackSize < 1) {
            Minecraft.setErrorMessage("stackSize is 0 for recipe output (" + recipe_output.getDisplayName() + ")");
        } else {
            float difficulty_per_unit = difficulty / recipe_output.stackSize;
        }

        output_item = recipe_output.getItem();
        if (output_item.containsMetal()) {
            if (output_item instanceof ItemIngot) {
                return;
            }

            if (output_item.getClass() == ItemKnife.class) {
                return;
            }

            if (output_item.getClass() == ItemBucket.class && hasAsComponent(recipe, ItemBucket.class)) {
                return;
            }

            recipe.setMaterialToCheckToolBenchHardnessAgainst(output_item.getHardestMetalMaterial());
        } else if (output_item.itemID == Block.planks.blockID) {
            recipe.setMaterialToCheckToolBenchHardnessAgainst(Material.wood);
        } else if (output_item.itemID == Block.workbench.blockID) {
            handleWorkbench(recipe, recipe_output);
        }

        // ric patch
        if (output_item instanceof ItemBlock itemBlock && itemBlock.getBlock() instanceof WorkbenchBlock workbenchBlock) {
            recipe.setMaterialToCheckToolBenchHardnessAgainst(workbenchBlock.getMaterialToCheckToolBenchHardnessAgainst());
        }
    }

    private static void handleWorkbench(CraftingRecipe recipe, ItemStack output_item_stack) {
        Material tool_material = BlockWorkbench.getToolMaterial(output_item_stack.getItemSubtype());
        if (tool_material.isMetal()) {
            Material next_strongest_material = null;
            float highest_durability_that_is_less_than_tool_material = 0.0F;

            for (int i = 0; i < BlockWorkbench.tool_materials.length; i++) {
                Material material = BlockWorkbench.tool_materials[i];
                float durability = ((IMaterialMixin) material).getDurability();
                if (material != Material.obsidian
                        && durability < ((IMaterialMixin) tool_material).getDurability()
                        && durability > highest_durability_that_is_less_than_tool_material) {
                    next_strongest_material = material;
                    highest_durability_that_is_less_than_tool_material = durability;
                }
            }

            recipe.setMaterialToCheckToolBenchHardnessAgainst(next_strongest_material);
        }
    }

    private static float handleRustedIron(Item item, float component_difficulty) {
        Item peer = Item.getMatchingItem(item.getClass(), Material.copper);
        if (peer != null) {
            if (item.getMaterialForDurability() == null) {
                Minecraft.setErrorMessage("addRecipe: getMaterialForDurability()==null for component " + item);
            }

            item.setCraftingDifficultyAsComponent(
                    peer.getCraftingDifficultyAsComponent(null) *
                            ((IMaterialMixin) item.getMaterialForDurability()).getDurability() /
                            ((IMaterialMixin) peer.getMaterialForDurability()).getDurability()
            );
            component_difficulty = item.getCraftingDifficultyAsComponent(null);
        }
        return component_difficulty;
    }

    @SuppressWarnings("deprecation")
    public static boolean hasAsComponent(CraftingRecipe recipe, Class<? extends Item> item_class) {
        List<Ingredient> ingredients = recipe.getIngredients();

        for (Ingredient ingredient : ingredients) {
            if (
                    ingredient.items()
                            .map(Holder::value)
                            .anyMatch(x -> x.getClass() == item_class)
            ) {
                return true;
            }
        }

        return false;
    }
}
