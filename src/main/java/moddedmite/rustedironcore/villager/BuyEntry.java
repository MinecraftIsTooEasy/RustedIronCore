package moddedmite.rustedironcore.villager;

import net.minecraft.*;

import java.util.Random;

public record BuyEntry(int id, float originalProbability) implements RecipeEntry {
    @Override
    public void apply(MerchantRecipeList recipeList, EntityVillager villager, Random rand) {
        addMerchantItem(recipeList, this.id, rand, villager.adjustProbability(this.originalProbability));
    }

    @SuppressWarnings("unchecked")
    private static void addMerchantItem(MerchantRecipeList par0MerchantRecipeList, int par1, Random par2Random, float par3) {
        if (par2Random.nextFloat() < par3) {
            par0MerchantRecipeList.add(new MerchantRecipe(getRandomSizedStack(par1, par2Random), Item.emerald));
        }
    }

    private static ItemStack getRandomSizedStack(int par0, Random par1Random) {
        return new ItemStack(par0, getRandomCountForItem(par0, par1Random), 0);
    }

    private static int getRandomCountForItem(int par0, Random par1Random) {
        Tuple var2 = (Tuple) EntityVillager.villagerStockList.get(par0);
        return var2 == null ? 1 : ((Integer) var2.getFirst() >= (Integer) var2.getSecond() ? (Integer) var2.getFirst() : (Integer) var2.getFirst() + par1Random.nextInt((Integer) var2.getSecond() - (Integer) var2.getFirst()));
    }
}
