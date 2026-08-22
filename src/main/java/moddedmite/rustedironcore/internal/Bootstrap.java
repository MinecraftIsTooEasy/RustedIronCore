package moddedmite.rustedironcore.internal;

import huix.glacier.api.entrypoint.IGameRegistry;
import moddedmite.rustedironcore.mixin.block.entity.ITileEntityMixin;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class Bootstrap {
    public static final Logger LOGGER = LogManager.getLogger(Bootstrap.class);

    public static void bootstrap() {
        Instant start = Instant.now();
        LOGGER.info("bootstrap started");

        StatList.nopInit();

        if (EntityList.getEntityID(EntityItem.class) == 0) {
            throwStage("entity");
        }
        if (!ITileEntityMixin.invokeGetClassToNameMap().containsKey(TileEntityChest.class)) {
            throwStage("block entity");
        }
        if (BiomeGenBase.biomeList[0] == null) {
            throwStage("biome");
        }
        if (Potion.get(1) == null) {
            throwStage("potion");
        }

        FishModLoader.invokeEntrypoints("registry", IGameRegistry.class, IGameRegistry::onGameRegistry);

        LOGGER.info("bootstrap finished in {} millis", Duration.between(start, Instant.now()).toMillis());
    }

    public static void logStage(String stage) {
        LOGGER.info("{} bootstrap done", stage);
    }

    public static void throwStage(String stage) {
        throw new IllegalStateException(stage + " registry failed");
    }
}
