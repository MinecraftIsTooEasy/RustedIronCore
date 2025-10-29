package moddedmite.rustedironcore.internal.unsafe;

import net.minecraft.CraftingResult;

public class CraftingResultAccess {
    public static boolean isRepair(CraftingResult result) {
        return result.isRepair();
    }
}
