package moddedmite.rustedironcore.world.entity.player;

import java.util.List;

import moddedmite.rustedironcore.annotations.NotVanilla;
import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.TypedInstance;
import moddedmite.rustedironcore.world.item.ItemInstance;
import moddedmite.rustedironcore.world.item.crafting.PlacementInfo;
import moddedmite.rustedironcore.world.item.crafting.Recipe;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import org.jspecify.annotations.Nullable;

public class StackedItemContents {
	private final StackedContents<Holder<Item>> raw = new StackedContents<>();

	@NotVanilla
	private static boolean isUsableForCrafting(final ItemStack item) {
		return !item.isItemDamaged() && !item.isItemEnchanted();
	}

	public void accountSimpleStack(final ItemStack itemStack) {
		if (isUsableForCrafting(itemStack)) {
			this.accountStack(itemStack);
		}
	}

	public void accountStack(final ItemStack itemStack) {
		this.accountStack(itemStack, itemStack.getMaxStackSize());
	}

	public void accountStack(final ItemStack itemStack, final int maxCount) {
		if (itemStack != null) {
			ItemInstance instance = TypedInstance.castItem(itemStack);
			int count = Math.min(maxCount, instance.count());
			this.raw.account(instance.typeHolder(), count);
		}
	}

	public boolean canCraft(final Recipe<?> recipe, final StackedContents.@Nullable Output<Holder<Item>> output) {
		return this.canCraft(recipe, 1, output);
	}

	public boolean canCraft(final Recipe<?> recipe, final int amount, final StackedContents.@Nullable Output<Holder<Item>> output) {
		PlacementInfo placementInfo = recipe.placementInfo();
		return placementInfo.isImpossibleToPlace() ? false : this.canCraft(placementInfo.ingredients(), amount, output);
	}

	public boolean canCraft(
		final List<? extends StackedContents.IngredientInfo<Holder<Item>>> contents, final StackedContents.@Nullable Output<Holder<Item>> output
	) {
		return this.canCraft(contents, 1, output);
	}

	private boolean canCraft(
		final List<? extends StackedContents.IngredientInfo<Holder<Item>>> contents, final int amount, final StackedContents.@Nullable Output<Holder<Item>> output
	) {
		return this.raw.tryPick(contents, amount, output);
	}

	public int getBiggestCraftableStack(final Recipe<?> recipe, final StackedContents.@Nullable Output<Holder<Item>> output) {
		return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, output);
	}

	public int getBiggestCraftableStack(final Recipe<?> recipe, final int maxSize, final StackedContents.@Nullable Output<Holder<Item>> output) {
		return this.raw.tryPickAll(recipe.placementInfo().ingredients(), maxSize, output);
	}

	public void clear() {
		this.raw.clear();
	}
}
