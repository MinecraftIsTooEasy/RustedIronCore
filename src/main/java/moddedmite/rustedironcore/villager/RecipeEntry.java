package moddedmite.rustedironcore.villager;

import net.minecraft.EntityVillager;
import net.minecraft.MerchantRecipeList;

import java.util.Random;

@FunctionalInterface
public interface RecipeEntry {
    void apply(MerchantRecipeList recipeList, EntityVillager villager, Random rand);
}
