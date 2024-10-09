package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.BlockBreakInfo;

public interface IGravelDropListener {
    // if not gravel, then it will be flint or special nuggets
    default float onDropAsGravelChanceModify(BlockBreakInfo info, float original) {
        return original;
    }

    // if not flint, then it will be special nuggets
    default float onDropAsFlintChanceModify(BlockBreakInfo info, float original) {
        return original;
    }

    // if not chip, then it will be a whole flint
    default float onDropFlintAsChipChanceModify(BlockBreakInfo info, float original) {
        return original;
    }

    // the nether gravel drops are a bit different
    default int onNetherGravelDropIDModify(BlockBreakInfo info, int original) {
        return original;
    }

    // you can get some achievements here
    default void onDropResult(BlockBreakInfo info, int id_dropped) {
    }
}
