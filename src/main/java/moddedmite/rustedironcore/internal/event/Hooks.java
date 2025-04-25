package moddedmite.rustedironcore.internal.event;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.*;
import moddedmite.rustedironcore.internal.config.RICConfig;

public class Hooks {
    public static void postCraftingRecipeRegister() {
        Handlers.PropertiesRegistry.publish();
        Handlers.Smelting.publish(new SmeltingRecipeRegisterEvent());
        Handlers.SpawnCondition.publish(new SpawnConditionRegisterEvent());
        Handlers.EntityTracker.publish(new EntityTrackerRegisterEvent());
        Handlers.LootTable.onFishingRegister();
        Handlers.TileEntityData.publish(new TileEntityDataTypeRegisterEvent());
        if (RICConfig.UseCustomDimension.get()) {
            Handlers.Dimension.publish(new DimensionRegisterEvent(Handlers.Dimension));
        }
    }
}
