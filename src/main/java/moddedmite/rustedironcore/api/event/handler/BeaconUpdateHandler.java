package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IBeaconUpdateHandler;
import net.minecraft.ItemStack;
import net.minecraft.TileEntityBeacon;

public class BeaconUpdateHandler extends AbstractHandler<IBeaconUpdateHandler> {
    public boolean onBlockValidModify(TileEntityBeacon tileEntityBeacon, int blockID, boolean original) {
        for (IBeaconUpdateHandler listener : this.listeners) {
            original = listener.onBlockValidModify(tileEntityBeacon, blockID, original);
        }
        return original;
    }

    public boolean onItemValidModify(TileEntityBeacon tileEntityBeacon, ItemStack itemStack, boolean original) {
        for (IBeaconUpdateHandler listener : this.listeners) {
            original = listener.onItemValidModify(tileEntityBeacon, itemStack, original);
        }
        return original;
    }
}
