package moddedmite.rustedironcore.api.block;

import moddedmite.rustedironcore.mixin.accessor.BlockAccessor;
import net.minecraft.BlockBreakInfo;
import net.minecraft.ItemStack;

import javax.annotation.Nullable;

public class BlockDrops {

    /**
     * Spawns a copy of a caller-selected stack on the server, preserving all item data.
     * Uses the native entity spawn path for game rules, pickup delay and explosion motion.
     * The caller determines the drop contents and probability.
     *
     * @return the number of items spawned, or zero when no drop is created
     */
    public static int dropStack(BlockBreakInfo info, @Nullable ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) return 0;
        ItemStack drop = stack.copy();
        if (drop.stackSize == 1 && !drop.hasDisplayName() && info.tile_entity != null) {
            String name = info.tile_entity.getCustomInvName();
            if (name != null) drop.setItemName(name);
        }
        return ((BlockAccessor) info.block).rusted_iron_core$dropBlockAsItem(info, drop) == null ? 0 : drop.stackSize;
    }
}
