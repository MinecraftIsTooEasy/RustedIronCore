package moddedmite.rustedironcore.api.util;

import net.xiaoyu233.fml.reload.utils.IdUtil;

public class IdUtilExtra extends IdUtil {
    private static int nextGameTypeID = 3;
    private static int nextVillagerProfessionID = 5;
    private static int nextPacket23Type = 800;

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
}
