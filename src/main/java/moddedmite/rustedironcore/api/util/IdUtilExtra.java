package moddedmite.rustedironcore.api.util;

import net.xiaoyu233.fml.reload.utils.IdUtil;

public class IdUtilExtra extends IdUtil {
    private static int nextGameTypeID = 3;
    private static int nextVillagerProfessionID = 5;
    private static int nextPacket23Type = 800;
    private static int nextTileEntityDataType = 5;
    private static int nextDimensionID = 2;
    private static int nextWorldType = 3;
    private static int nextSlabGroup = 4;

    public IdUtilExtra() {
    }

    public static int getNextGameTypeID() {
        return nextGameTypeID++;
    }

    public static int getNextVillagerProfessionID() {
        return nextVillagerProfessionID++;
    }

    public static int getNextPacket23Type() {
        return nextPacket23Type++;
    }

    public static int getNextTileEntityDataType() {
        return nextTileEntityDataType++;
    }

    public static int getNextDimensionID() {
        return nextDimensionID++;
    }

    public static int getNextWorldType() {
        return nextWorldType++;
    }

    public static int getNextSlabGroup() {
        return nextSlabGroup++;
    }
}
