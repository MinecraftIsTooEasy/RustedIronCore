package moddedmite.rustedironcore.mixin.util;

import huix.glacier.api.extension.item.IRetainableItem;
import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;
import moddedmite.rustedironcore.api.interfaces.IRecipeExtend;
import moddedmite.rustedironcore.property.ItemProperties;
import net.minecraft.*;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

@Mixin(SlotCrafting.class)
public abstract class SlotCraftingMixin extends Slot {
    @Shadow
    public CraftingResult crafting_result;

    @Shadow
    private int amountCrafted;

    public SlotCraftingMixin(IInventory inventory, int slot_index, int display_x, int display_y) {
        super(inventory, slot_index, display_x, display_y);
    }

    @Shadow
    protected abstract void onCrafting(ItemStack par1ItemStack, int par2);

    @Shadow
    @Final
    private IInventory craftMatrix;

    @Shadow
    private EntityPlayer thePlayer;

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack resultItemStack) {
        int consumption = this.crafting_result.consumption;
        this.amountCrafted = resultItemStack.stackSize;
        this.onCrafting(resultItemStack);
        par1EntityPlayer.inventory.addItemStackToInventoryOrDropIt(resultItemStack.copy());
        int xp_reclaimed = 0;
        List<CraftingRecipeRegisterEvent.ConsumeRule> consumeOverride = this.getConsumeOverride();
        for (int slotIndex = 0; slotIndex < this.craftMatrix.getSizeInventory(); ++slotIndex) {
            ItemStack itemStackInSlot = this.craftMatrix.getStackInSlot(slotIndex);
            if (itemStackInSlot == null) continue;
            Item item = itemStackInSlot.getItem();
            if (item instanceof ItemCoin coin) {
                xp_reclaimed += coin.getExperienceValue();
            }
            this.craftMatrix.decrStackSize(slotIndex, consumption);
            if (itemStackInSlot.getItem().hasContainerItem()) {
                ItemStack emptyContainer = new ItemStack(itemStackInSlot.getItem().getContainerItem());
                Item container_item = emptyContainer.getItem();
                if (container_item.getClass() == resultItemStack.getItem().getClass() || itemStackInSlot.getItem().doesContainerItemLeaveCraftingGrid(itemStackInSlot) && this.thePlayer.inventory.addItemStackToInventory(emptyContainer))
                    continue;
                if (this.craftMatrix.getStackInSlot(slotIndex) == null) {
                    this.craftMatrix.setInventorySlotContents(slotIndex, emptyContainer);
                    continue;
                }
                this.thePlayer.dropPlayerItem(emptyContainer);
                continue;
            }
            if (itemStackInSlot.itemID == Block.workbench.blockID) {
                this.thePlayer.inventory.addItemStackToInventoryOrDropIt(BlockWorkbench.getBlockComponent(itemStackInSlot.getItemSubtype()));
                continue;
            }
            if (itemStackInSlot.getItem() instanceof IRetainableItem iRetainableItem) {
                this.craftMatrix.setInventorySlotContents(slotIndex, iRetainableItem.getItemStackAfterCrafting(itemStackInSlot));
            }
            Optional<UnaryOperator<ItemStack>> optional = ItemProperties.CraftConsumeOverride.getOptional(itemStackInSlot.getItem());
            if (optional.isPresent()) {
                this.craftMatrix.setInventorySlotContents(slotIndex, optional.get().apply(itemStackInSlot));
                continue;
            }
            if (consumeOverride != null) {
                Optional<CraftingRecipeRegisterEvent.ConsumeRule> optional1 = consumeOverride.stream().filter(x -> x.matches(itemStackInSlot)).findFirst();
                if (optional1.isPresent() && this.craftMatrix.getStackInSlot(slotIndex) == null) {
                    this.craftMatrix.setInventorySlotContents(slotIndex, optional1.get().apply(itemStackInSlot));
                    continue;
                }
            }
        }
        if (xp_reclaimed > 0) {
            par1EntityPlayer.addExperience(xp_reclaimed, true, false);
        }
    }

    @Nullable
    @Unique
    private List<CraftingRecipeRegisterEvent.ConsumeRule> getConsumeOverride() {
        IRecipe recipe = this.crafting_result.recipe;
        if (recipe instanceof ShapedRecipes shaped) {
            return ((IRecipeExtend) shaped).ric$GetConsumeRules();
        }
        if (recipe instanceof ShapelessRecipes shapeless) {
            return ((IRecipeExtend) shapeless).ric$GetConsumeRules();
        }
        return null;
    }
}
