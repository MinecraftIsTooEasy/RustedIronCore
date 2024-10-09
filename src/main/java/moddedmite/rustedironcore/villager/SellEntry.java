package moddedmite.rustedironcore.villager;

import net.minecraft.*;

import java.util.Random;

public record SellEntry(int id, float originalProbability) implements RecipeEntry {
    @Override
    public void apply(MerchantRecipeList recipeList, EntityVillager villager, Random rand) {
        addBlacksmithItem(recipeList, this.id, rand, villager.adjustProbability(this.originalProbability));
    }

    @SuppressWarnings("unchecked")
    private static void addBlacksmithItem(MerchantRecipeList par0MerchantRecipeList, int par1, Random par2Random, float par3) {
        if (par2Random.nextFloat() < par3) {
            int var4 = getRandomCountForBlacksmithItem(par1, par2Random);
            ItemStack var5;
            ItemStack var6;
            if (var4 < 0) {
                var5 = new ItemStack(Item.emerald.itemID, 1, 0);
                var6 = new ItemStack(par1, -var4, 0);
                if (var6.stackSize > var6.getMaxStackSize()) {
                    var6.stackSize = var6.getMaxStackSize();
                }
            } else {
                var5 = new ItemStack(Item.emerald.itemID, var4, 0);
                var6 = new ItemStack(par1, 1, 0);
                if (var5.stackSize > var5.getMaxStackSize()) {
                    var5.stackSize = var5.getMaxStackSize();
                }
            }
            par0MerchantRecipeList.add(new MerchantRecipe(var5, var6));
        }
    }

    private static int getRandomCountForBlacksmithItem(int par0, Random par1Random) {
        Tuple var2 = (Tuple) EntityVillager.blacksmithSellingList.get(par0);
        return var2 == null ? 1 : ((Integer) var2.getFirst() >= (Integer) var2.getSecond() ? (Integer) var2.getFirst() : (Integer) var2.getFirst() + par1Random.nextInt((Integer) var2.getSecond() - (Integer) var2.getFirst()));
    }
}
