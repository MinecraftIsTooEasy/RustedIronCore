package moddedmite.rustedironcore.internal;

import huix.glacier.api.entrypoint.IGameRegistry;
import moddedmite.rustedironcore.api.RicRegistry;
import moddedmite.rustedironcore.internal.config.RICConfig;
import moddedmite.rustedironcore.mixin.block.entity.ITileEntityMixin;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.reload.event.EntityRegisterEvent;
import net.xiaoyu233.fml.reload.event.MITEEvents;
import net.xiaoyu233.fml.reload.event.TileEntityRegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class Bootstrap {
    public static final Logger LOGGER = LogManager.getLogger(Bootstrap.class);

    public static boolean preloadGameObjects() {
        return RICConfig.PreLoadGameObjects.get();
    }

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
        if (AchievementList.getAchievementForId(0) == null) {
            throwStage("achievement");
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

    public static void applyPatch() {
        RicRegistry ricRegistry = RicRegistry.getInstance();

        MITEEvents.MITE_EVENT_BUS.post(new EntityRegisterEvent(
                (clazz, namespace, name, id) -> ricRegistry.registerEntity(clazz, new ResourceLocation(namespace, name), id),
                (clazz, namespace, name, id, color1, color2) -> {
                    ricRegistry.registerEntity(clazz, new ResourceLocation(namespace, name), id);
                    ricRegistry.registerEntityEgg(clazz, color1, color2);
                }
        ));

        MITEEvents.MITE_EVENT_BUS.post(new TileEntityRegisterEvent(
                ITileEntityMixin::invokeAddMapping
        ));

        StatList.initCraftableStats();
    }
}
