package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.RustedIronCore;
import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.TradingRegisterEvent;
import moddedmite.rustedironcore.random.RandomUtil;
import moddedmite.rustedironcore.villager.VillagerSettings;
import net.minecraft.*;

import java.util.*;
import java.util.function.Consumer;

public class TradingHandler extends EventHandler<TradingRegisterEvent> {

    private final Map<Integer, VillagerSettings> professionMap = new HashMap<>();

    public TradingHandler() {
        this.register(VanillaListener);
    }

    public int getRandomProfession(Random rand) {
        List<VillagerSettings> unbannedList = this.professionMap.values().stream().filter(x -> !x.isBanned()).toList();
        return RandomUtil.getRandom(unbannedList, rand).getProfession();
    }

    public Optional<VillagerSettings> getForProfession(int profession) {
        return Optional.ofNullable(this.professionMap.getOrDefault(profession, null));
    }

    public void registerProfession(int profession, VillagerSettings villagerSettings) {
        this.professionMap.put(profession, villagerSettings);
    }

    public void addRecipeToList(MerchantRecipeList recipeList, EntityVillager villager, Random rand) {
        VillagerSettings villagerSettings = this.professionMap.get(villager.getProfession());
        if (villagerSettings == null) {
            RustedIronCore.logger.warn("unhandled villager profession: '{}'", villager.getProfession());
        } else {
            villagerSettings.getRecipeEntries().forEach(x -> x.apply(recipeList, villager, rand));
        }
    }

    public static final VillagerSettings Farmer = new VillagerSettings(0, VillagerSettings.FarmerTexture);
    public static final VillagerSettings Librarian = new VillagerSettings(1, VillagerSettings.LibrarianTexture);
    public static final VillagerSettings Priest = new VillagerSettings(2, VillagerSettings.PriestTexture);
    public static final VillagerSettings Smith = new VillagerSettings(3, VillagerSettings.SmithTexture);
    public static final VillagerSettings Butcher = new VillagerSettings(4, VillagerSettings.ButcherTexture);

    public static final Consumer<TradingRegisterEvent> VanillaListener = event -> {

        event.registerProfession(0, Farmer);
        event.registerProfession(1, Librarian);
        event.registerProfession(2, Priest);
        event.registerProfession(3, Smith);
        event.registerProfession(4, Butcher);

        Farmer.buyEntry(Item.wheat.itemID, 0.9F);
        Farmer.buyEntry(Item.wheat.itemID, 0.9F);
        Farmer.buyEntry(Block.cloth.blockID, 0.5F);
        Farmer.buyEntry(Item.chickenRaw.itemID, 0.5F);
        Farmer.buyEntry(Item.fishCooked.itemID, 0.4F);
        Farmer.sellEntry(Item.bread.itemID, 0.9F);
        Farmer.sellEntry(Item.melon.itemID, 0.3F);
        Farmer.sellEntry(Item.appleRed.itemID, 0.3F);
        Farmer.sellEntry(Item.cookie.itemID, 0.3F);
        Farmer.sellEntry(Item.shears.itemID, 0.3F);
        Farmer.sellEntry(Item.flintAndSteel.itemID, 0.3F);
        Farmer.sellEntry(Item.chickenCooked.itemID, 0.3F);
        Farmer.sellEntry(Item.arrowFlint.itemID, 0.5F);
        Farmer.addEntry((recipeList, villager, rand) -> {
            if (rand.nextFloat() < villager.adjustProbability(0.5F)) {
                recipeList.add(new MerchantRecipe(new ItemStack(Block.gravel, 4), new ItemStack(Item.emerald), new ItemStack(Item.flint.itemID, 4 + rand.nextInt(2), 0)));
            }
        });


        Librarian.buyEntry(Item.paper.itemID, 0.8F);
        Librarian.buyEntry(Item.book.itemID, 0.8F);
        Librarian.buyEntry(Item.writtenBook.itemID, 0.3F);
        Librarian.sellEntry(Block.bookShelf.blockID, 0.8F);
        Librarian.sellEntry(Block.glass.blockID, 0.2F);
        Librarian.sellEntry(Item.compass.itemID, 0.2F);
        Librarian.sellEntry(Item.pocketSundial.itemID, 0.2F);
        Librarian.addEntry((recipeList, villager, rand) -> {
            if (rand.nextFloat() < villager.adjustProbability(0.07F)) {
                Enchantment var8 = Enchantment.enchantmentsBookList[rand.nextInt(Enchantment.enchantmentsBookList.length)];
                int var10 = MathHelper.getRandomIntegerInRange(rand, 1, var8.getNumLevels());
                ItemStack var11 = Item.enchantedBook.getEnchantedItemStack(new EnchantmentData(var8, var10));
                int var6 = 2 + rand.nextInt(5 + var10 * 10) + 3 * var10;
                recipeList.add(new MerchantRecipe(new ItemStack(Item.book), new ItemStack(Item.emerald, var6), var11));
            }
        });


        Priest.sellEntry(Item.eyeOfEnder.itemID, 0.3F);
        Priest.sellEntry(Item.redstone.itemID, 0.4F);
        Priest.sellEntry(Block.glowStone.blockID, 0.3F);
        Priest.addEntry((recipeList, villager, rand) -> {
            int[] var3 = new int[]{Item.swordCopper.itemID, Item.swordIron.itemID, Item.plateCopper.itemID, Item.plateIron.itemID, Item.axeCopper.itemID, Item.axeIron.itemID, Item.pickaxeCopper.itemID, Item.pickaxeIron.itemID};
            int var5 = var3.length;
            int var6 = 0;
            while (true) {
                if (var6 >= var5) {
                    return;
                }
                int var7 = var3[var6];
                if (rand.nextFloat() < villager.adjustProbability(0.05F)) {
                    recipeList.add(new MerchantRecipe(new ItemStack(var7, 1, 0), new ItemStack(Item.emerald, 2 + rand.nextInt(3), 0), EnchantmentHelper.addRandomEnchantment(rand, new ItemStack(var7, 1, 0), 5 + rand.nextInt(15))));
                }
                ++var6;
            }
        });


        Smith.buyEntry(Item.coal.itemID, 0.7F);
        Smith.buyEntry(Item.ingotIron.itemID, 0.5F);
        Smith.buyEntry(Item.ingotGold.itemID, 0.5F);
        Smith.sellEntry(Item.swordIron.itemID, 0.5F);
        Smith.sellEntry(Item.axeIron.itemID, 0.3F);
        Smith.sellEntry(Item.pickaxeIron.itemID, 0.5F);
        Smith.sellEntry(Item.shovelIron.itemID, 0.2F);
        Smith.sellEntry(Item.hoeIron.itemID, 0.2F);
        Smith.sellEntry(Item.helmetIron.itemID, 0.2F);
        Smith.sellEntry(Item.plateIron.itemID, 0.2F);
        Smith.sellEntry(Item.legsIron.itemID, 0.2F);
        Smith.sellEntry(Item.bootsIron.itemID, 0.2F);
        Smith.sellEntry(Item.pickaxeCopper.itemID, 0.5F);
        Smith.sellEntry(Item.shovelCopper.itemID, 0.2F);
        Smith.sellEntry(Item.axeCopper.itemID, 0.3F);
        Smith.sellEntry(Item.hoeCopper.itemID, 0.2F);
        Smith.sellEntry(Item.daggerCopper.itemID, 0.5F);
        Smith.sellEntry(Item.swordCopper.itemID, 0.5F);
        Smith.sellEntry(Item.daggerIron.itemID, 0.5F);
        Smith.sellEntry(Item.helmetCopper.itemID, 0.2F);
        Smith.sellEntry(Item.plateCopper.itemID, 0.2F);
        Smith.sellEntry(Item.legsCopper.itemID, 0.2F);
        Smith.sellEntry(Item.bootsCopper.itemID, 0.2F);
        Smith.sellEntry(Item.helmetChainCopper.itemID, 0.1F);
        Smith.sellEntry(Item.plateChainCopper.itemID, 0.1F);
        Smith.sellEntry(Item.legsChainCopper.itemID, 0.1F);
        Smith.sellEntry(Item.bootsChainCopper.itemID, 0.1F);
        Smith.sellEntry(Item.helmetChainIron.itemID, 0.1F);
        Smith.sellEntry(Item.plateChainIron.itemID, 0.1F);
        Smith.sellEntry(Item.legsChainIron.itemID, 0.1F);
        Smith.sellEntry(Item.bootsChainIron.itemID, 0.1F);


        Butcher.buyEntry(Item.coal.itemID, 0.7F);
        Butcher.buyEntry(Item.porkRaw.itemID, 0.5F);
        Butcher.buyEntry(Item.beefRaw.itemID, 0.5F);
        Butcher.buyEntry(Item.lambchopRaw.itemID, 0.5F);
        Butcher.sellEntry(Item.saddle.itemID, 0.1F);
        Butcher.sellEntry(Item.plateLeather.itemID, 0.3F);
        Butcher.sellEntry(Item.bootsLeather.itemID, 0.3F);
        Butcher.sellEntry(Item.helmetLeather.itemID, 0.3F);
        Butcher.sellEntry(Item.legsLeather.itemID, 0.3F);
        Butcher.sellEntry(Item.porkCooked.itemID, 0.3F);
        Butcher.sellEntry(Item.beefCooked.itemID, 0.3F);
        Butcher.sellEntry(Item.lambchopCooked.itemID, 0.3F);
    };

}
