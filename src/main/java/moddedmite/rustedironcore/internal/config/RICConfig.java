package moddedmite.rustedironcore.internal.config;

import moddedmite.rustedironcore.RustedIronCore;
import net.xiaoyu233.fml.config.ConfigEntry;
import net.xiaoyu233.fml.config.ConfigRoot;
import net.xiaoyu233.fml.util.FieldReference;

import java.io.File;

public class RICConfig {
    public static final FieldReference<Boolean> StatementOnLogin = new FieldReference<>(true);

    public static final ConfigRoot ROOT = ConfigRoot.create(1)
            .addEntry(ConfigEntry.of("第一次进入世界时显示锈铁核心声明", StatementOnLogin));

    public static final File CONFIG_FILE = new File(RustedIronCore.MOD_ID + ".json");
}
