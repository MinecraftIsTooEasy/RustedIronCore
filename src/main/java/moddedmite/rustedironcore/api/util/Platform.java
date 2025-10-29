package moddedmite.rustedironcore.api.util;

import moddedmite.rustedironcore.internal.unsafe.MiteReleaseAccess;

public class Platform {
    public static boolean isExperimental() {
        return MiteReleaseAccess.isExperimental();
    }
}
