package moddedmite.rustedironcore.world.item.crafting;

import moddedmite.rustedironcore.world.inventory.CraftingContainer;
import net.minecraft.CraftingResult;
import net.minecraft.InventoryCrafting;
import net.minecraft.ItemStack;
import net.minecraft.World;

public interface CraftingRecipe extends Recipe<CraftingInput> {
    @Override
    default boolean matches(InventoryCrafting inventory, World world) {
        return this.matches(CraftingContainer.wrap(inventory).asCraftInput(), world);
    }

    @Override
    default ItemStack getRecipeOutput() {
        return this.assemble(null);
    }

    @Override
    default CraftingResult getCraftingResult(InventoryCrafting inventory) {
        CraftingInput input = CraftingContainer.wrap(inventory).asCraftInput();
        ItemStack stack = this.assemble(input);
//        if (this.field_92101_f) {
//            for (int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory(); var3++) {
//                ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);
//                if (var4 != null && var4.hasTagCompound()) {
//                    stack.setTagCompound((NBTTagCompound)var4.stackTagCompound.copy());
//                }
//            }
//        }
        return new CraftingResult(stack, this.getUnmodifiedDifficulty(), this.getSkillsets(), this);
    }

    @Override
    default RecipeType<CraftingRecipe> getType() {
        return RecipeType.CRAFTING;
    }

//    @Override
//    RecipeSerializer<? extends CraftingRecipe> getSerializer();

    CraftingBookCategory category();

//    default NonNullList<ItemStack> getRemainingItems(final CraftingInput input) {
//        return defaultCraftingReminder(input);
//    }
//
//    static NonNullList<ItemStack> defaultCraftingReminder(final CraftingInput input) {
//        NonNullList<ItemStack> result = NonNullList.withSize(input.size(), ItemStack.EMPTY);
//
//        for (int slot = 0; slot < result.size(); slot++) {
//            Item item = input.getItem(slot).getItem();
//            ItemStackTemplate remainder = item.getCraftingRemainder();
//            result.set(slot, remainder != null ? remainder.create() : ItemStack.EMPTY);
//        }
//
//        return result;
//    }

    @Override
    default RecipeBookCategory recipeBookCategory() {
        return switch (this.category()) {
            case BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
            case EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
            case REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
            case MISC -> RecipeBookCategories.CRAFTING_MISC;
        };
    }

    record CraftingBookInfo(CraftingBookCategory category,
                            String group) implements Recipe.BookInfo<CraftingBookCategory> {
//        public static final MapCodec<CraftingRecipe.CraftingBookInfo> MAP_CODEC = Recipe.BookInfo.mapCodec(
//                CraftingBookCategory.CODEC, CraftingBookCategory.MISC, CraftingRecipe.CraftingBookInfo::new
//        );
//        public static final StreamCodec<RegistryFriendlyByteBuf, CraftingRecipe.CraftingBookInfo> STREAM_CODEC = Recipe.BookInfo.streamCodec(
//                CraftingBookCategory.STREAM_CODEC, CraftingRecipe.CraftingBookInfo::new
//        );
    }
}
