package moddedmite.rustedironcore.api.event.listener;

import net.minecraft.BlockBreakInfo;

public interface IGravelDropListener {
    default int onNetherGravelDropIDModify(BlockBreakInfo info, int original) {
        return original;
    }
}
