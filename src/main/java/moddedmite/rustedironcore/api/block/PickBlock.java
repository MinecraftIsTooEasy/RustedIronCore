package moddedmite.rustedironcore.api.block;

import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.World;

import javax.annotation.Nullable;

/** Provides the item represented by a placed block for client-side middle-click selection. */
public interface PickBlock {

    /**
     * Returns the item to select, or null to prevent selection.
     * Creative selection copies the item's data while preserving existing stack counts.
     */
    @Nullable
    ItemStack getPickBlock(World world, int x, int y, int z, EntityPlayer player);
}
