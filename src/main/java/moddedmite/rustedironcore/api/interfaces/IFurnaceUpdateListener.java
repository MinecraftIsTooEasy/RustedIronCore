package moddedmite.rustedironcore.api.interfaces;

import net.minecraft.TileEntityFurnace;

public interface IFurnaceUpdateListener {
    default void onFurnaceUpdatePre(TileEntityFurnace tileEntityFurnace) {
    }

    default int onFurnaceBurnTimeDecreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        return original;
    }

    default boolean onFurnaceBeginToBurn(TileEntityFurnace tileEntityFurnace, boolean original) {
        return original;
    }

    default void onFurnaceFuelConsumed(TileEntityFurnace tileEntityFurnace) {
    }

    default void onFurnaceCookTimeAdd(TileEntityFurnace tileEntityFurnace) {
    }

    default int onFurnaceCookTimeIncreaseModify(TileEntityFurnace tileEntityFurnace, int original) {
        return original;
    }

    default int onFurnaceCookTimeTargetModify(TileEntityFurnace tileEntityFurnace, int original) {
        return original;
    }

    default void onFurnaceCookSuccess(TileEntityFurnace tileEntityFurnace) {
    }
}
