package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.AbstractHandler;
import moddedmite.rustedironcore.api.event.listener.IFurnaceUpdateListener;
import net.minecraft.TileEntityFurnace;

public class FurnaceUpdateHandler extends AbstractHandler<IFurnaceUpdateListener> {
    public void onFurnaceUpdatePre(TileEntityFurnace tileEntityFurnace) {
        this.listeners.forEach(x -> x.onFurnaceUpdatePre(tileEntityFurnace));
    }

    public int onFurnaceBurnTimeDecreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.listeners) {
            original = furnaceUpdateHandler.onFurnaceBurnTimeDecreaseModify(tileEntityFurnace, original);
        }
        return original;
    }

    public boolean onFurnaceBeginToBurn(TileEntityFurnace tileEntityFurnace, boolean original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.listeners) {
            original = furnaceUpdateHandler.onFurnaceBeginToBurn(tileEntityFurnace, original);
        }
        return original;
    }

    public void onFurnaceFuelConsumed(TileEntityFurnace tileEntityFurnace) {
        this.listeners.forEach(x -> x.onFurnaceFuelConsumed(tileEntityFurnace));
    }

    public void onFurnaceCookTimeAdd(TileEntityFurnace tileEntityFurnace) {
        this.listeners.forEach(x -> x.onFurnaceCookTimeAdd(tileEntityFurnace));
    }

    public int onFurnaceCookTimeIncreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.listeners) {
            original = furnaceUpdateHandler.onFurnaceCookTimeIncreaseModify(tileEntityFurnace, original);
        }
        return original;
    }

    public int onFurnaceCookTimeTargetModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.listeners) {
            original = furnaceUpdateHandler.onFurnaceCookTimeTargetModify(tileEntityFurnace, original);
        }
        return original;
    }

    public void onFurnaceCookSuccess(TileEntityFurnace tileEntityFurnace) {
        this.listeners.forEach(x -> x.onFurnaceCookSuccess(tileEntityFurnace));
    }
}
