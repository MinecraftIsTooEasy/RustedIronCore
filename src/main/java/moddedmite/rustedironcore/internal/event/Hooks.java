package moddedmite.rustedironcore.internal.event;

import huix.glacier.api.entrypoint.IGameRegistry;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.StagedHandler;
import moddedmite.rustedironcore.api.event.events.*;
import moddedmite.rustedironcore.internal.Bootstrap;
import moddedmite.rustedironcore.internal.client.ClientBootstrap;
import moddedmite.rustedironcore.internal.config.RICConfig;
import net.xiaoyu233.fml.FishModLoader;

public class Hooks {
    public static void preClientLaunch() {
        if (!RICConfig.PreLoadGameObjects.get()) return;
        Bootstrap.bootstrap();
        ClientBootstrap.bootstrap();
    }

    public static void preServerLaunch() {
        if (!RICConfig.PreLoadGameObjects.get()) return;
        Bootstrap.bootstrap();
    }

    public static void postCraftingRecipeRegister() {
        if (!RICConfig.PreLoadGameObjects.get()) {
            FishModLoader.invokeEntrypoints("registry", IGameRegistry.class, IGameRegistry::onGameRegistry);
        }
        ((StagedHandler) Handlers.PropertiesRegistry).publish();
        Handlers.Smelting.publish(new SmeltingRecipeRegisterEvent());
        Handlers.SpawnCondition.publish(new SpawnConditionRegisterEvent());
        Handlers.EntityTracker.publish(new EntityTrackerRegisterEvent());
        Handlers.LootTable.onFishingRegister();
        Handlers.TileEntityData.publish(new TileEntityDataTypeRegisterEvent());
        if (RICConfig.UseCustomDimension.get()) {
            Handlers.Dimension.publish(new DimensionRegisterEvent(Handlers.Dimension));
        }
        Bootstrap.logStage("recipe");
    }
}
