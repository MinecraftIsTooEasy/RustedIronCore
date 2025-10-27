package moddedmite.rustedironcore.internal.config;

import moddedmite.rustedironcore.RustedIronCore;
import net.xiaoyu233.fml.config.ConfigEntry;
import net.xiaoyu233.fml.config.ConfigRegistry;
import net.xiaoyu233.fml.config.ConfigRoot;
import net.xiaoyu233.fml.util.FieldReference;

import java.io.File;

public class RICConfig {
    public static final ConfigRegistry INSTANCE;

    public static final FieldReference<Boolean> StatementOnLogin = new FieldReference<>(true);
    public static final FieldReference<Boolean> WorldGenDelegate = new FieldReference<>(false);
    public static final FieldReference<Boolean> UseCustomDimension = new FieldReference<>(false);
    public static final FieldReference<Boolean> ApplyPacketPatches = new FieldReference<>(true);

    public static final ConfigRoot ROOT = ConfigRoot.create(1)
            .addEntry(ConfigEntry.of("StatementOnLogin", StatementOnLogin).withComment("第一次进入世界时显示锈铁核心声明"))
            .addEntry(ConfigEntry.of("WorldGenDelegate", WorldGenDelegate).withComment("世界生成代理"))
            .addEntry(ConfigEntry.of("UseCustomDimension", UseCustomDimension).withComment("使用自定义维度"))
            .addEntry(ConfigEntry.of("ApplyPacketPatches", ApplyPacketPatches).withComment("应用网络包补丁"));

    public static final File CONFIG_FILE = new File(RustedIronCore.MOD_NAME + ".json");

    static {
        INSTANCE = new ConfigRegistry(RICConfig.ROOT, RICConfig.CONFIG_FILE);
        INSTANCE.reloadConfig();
    }
}
