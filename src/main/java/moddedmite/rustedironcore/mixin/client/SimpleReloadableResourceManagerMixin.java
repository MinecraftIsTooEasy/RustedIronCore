package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.api.resource.ResourcePackOrder;
import net.minecraft.ResourceManager;
import net.minecraft.ResourcePack;
import net.minecraft.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin {

    @Inject(method = "clearResources", at = @At("HEAD"))
    private void clearPackOrder(CallbackInfo ci) {
        ResourcePackOrder.clear((ResourceManager) this);
    }

    @Inject(method = "reloadResourcePack", at = @At("HEAD"))
    private void recordPackOrder(ResourcePack pack, CallbackInfo ci) {
        ResourcePackOrder.add((ResourceManager) this, pack);
    }
}
