package moddedmite.rustedironcore.world.inventory;

import moddedmite.rustedironcore.mixins.interfaces.RicInventoryCrafting;
import moddedmite.rustedironcore.mixins.interfaces.RicInventoryCraftingImpl;
import moddedmite.rustedironcore.world.entity.player.StackedItemContents;
import moddedmite.rustedironcore.world.item.crafting.CraftingInput;
import net.minecraft.InventoryCrafting;
import net.minecraft.ItemStack;

import java.util.Arrays;
import java.util.List;

public interface CraftingContainer extends /*IInventory,*/ StackedContentsCompatible {
    int getWidth();

    int getHeight();

    List<ItemStack> getItems();

    default CraftingInput asCraftInput() {
        return this.asPositionedCraftInput().input();
    }

    default CraftingInput.Positioned asPositionedCraftInput() {
        return CraftingInput.ofPositioned(this.getWidth(), this.getHeight(), this.getItems());
    }

    static CraftingContainer wrap(InventoryCrafting inventory) {
        RicInventoryCrafting instance = RicInventoryCraftingImpl.cast(inventory);
        return new CraftingContainer() {
            @Override
            public int getWidth() {
                return instance.getWidth();
            }

            @Override
            public int getHeight() {
                return instance.getHeight();
            }

            @Override
            public List<ItemStack> getItems() {
                return Arrays.asList(inventory.getInventory());// allow nulls
            }

            @Override
            public void fillStackedContents(StackedItemContents contents) {
                for (ItemStack itemStack : this.getItems()) {
                    contents.accountSimpleStack(itemStack);
                }
            }
        };
    }
}
