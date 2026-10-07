package moddedmite.rustedironcore.internal.event;

import huix.glacier.api.entrypoint.IGameRegistry;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.StagedHandler;
import moddedmite.rustedironcore.api.event.events.*;
import moddedmite.rustedironcore.internal.Bootstrap;
import moddedmite.rustedironcore.internal.client.ClientBootstrap;
import moddedmite.rustedironcore.internal.config.RICConfig;
import net.fabricmc.api.EnvType;
import net.xiaoyu233.fml.FishModLoader;

public class Hooks {
    public static void preEntrypoints(EnvType type) {
        if (!Bootstrap.preloadGameObjects()) return;
        Bootstrap.bootstrap();
        if (type == EnvType.CLIENT) {
            ClientBootstrap.bootstrap();
        }
    }

    public static void postEntrypoints(EnvType type) {
        if (!Bootstrap.preloadGameObjects()) return;
        Bootstrap.applyPatch();
    }

    public static void postCraftingRecipeRegister() {
        if (!Bootstrap.preloadGameObjects()) {
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
        BiomeDecorationRegisterEvent biomeDecorationRegisterEvent = new BiomeDecorationRegisterEvent();
        Handlers.BiomeDecoration.publish(biomeDecorationRegisterEvent);
        Handlers.BiomeDecoration.buildMap(biomeDecorationRegisterEvent.getMap());
        Handlers.OreGeneration.publish(new OreGenerationRegisterEvent(Handlers.OreGeneration));
    }
}
