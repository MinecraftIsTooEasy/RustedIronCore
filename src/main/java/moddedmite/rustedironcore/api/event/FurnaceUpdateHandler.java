package moddedmite.rustedironcore.api.event;

import moddedmite.rustedironcore.api.interfaces.IFurnaceUpdateListener;
import net.minecraft.TileEntityFurnace;

import java.util.ArrayList;
import java.util.List;

public class FurnaceUpdateHandler {
    private static final FurnaceUpdateHandler INSTANCE = new FurnaceUpdateHandler();

    public static FurnaceUpdateHandler getInstance() {
        return INSTANCE;
    }

    private final List<IFurnaceUpdateListener> furnaceUpdateHandlers = new ArrayList<>();

    public void registerFurnaceUpdateListener(IFurnaceUpdateListener listener) {
        if (!this.furnaceUpdateHandlers.contains(listener)) {
            this.furnaceUpdateHandlers.add(listener);
        }
    }

    public void unregisterFurnaceUpdateListener(IFurnaceUpdateListener listener) {
        this.furnaceUpdateHandlers.remove(listener);
    }

    public void onFurnaceUpdatePre(TileEntityFurnace tileEntityFurnace) {
        this.furnaceUpdateHandlers.forEach(x -> x.onFurnaceUpdatePre(tileEntityFurnace));
    }

    public int onFurnaceBurnTimeDecreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.furnaceUpdateHandlers) {
            original = furnaceUpdateHandler.onFurnaceBurnTimeDecreaseModify(tileEntityFurnace, original);
        }
        return original;
    }

    public boolean onFurnaceBeginToBurn(TileEntityFurnace tileEntityFurnace, boolean original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.furnaceUpdateHandlers) {
            original = furnaceUpdateHandler.onFurnaceBeginToBurn(tileEntityFurnace, original);
        }
        return original;
    }

    public void onFurnaceFuelConsumed(TileEntityFurnace tileEntityFurnace) {
        this.furnaceUpdateHandlers.forEach(x -> x.onFurnaceFuelConsumed(tileEntityFurnace));
    }

    public void onFurnaceCookTimeAdd(TileEntityFurnace tileEntityFurnace) {
        this.furnaceUpdateHandlers.forEach(x -> x.onFurnaceCookTimeAdd(tileEntityFurnace));
    }

    public int onFurnaceCookTimeIncreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.furnaceUpdateHandlers) {
            original = furnaceUpdateHandler.onFurnaceCookTimeIncreaseModify(tileEntityFurnace, original);
        }
        return original;
    }

    public int onFurnaceCookTimeTargetModify(TileEntityFurnace tileEntityFurnace, int original) {
        for (IFurnaceUpdateListener furnaceUpdateHandler : this.furnaceUpdateHandlers) {
            original = furnaceUpdateHandler.onFurnaceCookTimeTargetModify(tileEntityFurnace, original);
        }
        return original;
    }

    public void onFurnaceCookSuccess(TileEntityFurnace tileEntityFurnace) {
        this.furnaceUpdateHandlers.forEach(x -> x.onFurnaceCookSuccess(tileEntityFurnace));
    }
}
