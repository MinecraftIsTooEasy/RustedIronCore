package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.ItemStack;
import net.minecraft.TileEntityBeacon;

public interface IBeaconUpdateHandler {
    default boolean onBlockValidModify(TileEntityBeacon tileEntityBeacon, int blockID, boolean original) {
        return original;
    }

    default boolean onItemValidModify(TileEntityBeacon tileEntityBeacon, ItemStack itemStack, boolean original) {
        return original;
    }
}
