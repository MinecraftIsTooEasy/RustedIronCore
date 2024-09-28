package moddedmite.rustedironcore.mixin.other.util;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.SmeltingRecipeRegisterEvent;
import moddedmite.rustedironcore.api.event.events.SpawnConditionRegisterEvent;
import net.minecraft.CraftingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        Handlers.PropertiesRegistry.getListeners().forEach(Runnable::run);
        Handlers.Smelting.post(new SmeltingRecipeRegisterEvent());
        Handlers.SpawnCondition.post(new SpawnConditionRegisterEvent());
    }
}
