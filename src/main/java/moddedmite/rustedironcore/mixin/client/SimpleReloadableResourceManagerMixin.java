package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.api.resource.ResourcePackOrder;
import moddedmite.rustedironcore.api.resource.PackTextureResources;
import net.minecraft.MetadataSerializer;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.ResourcePack;
import net.minecraft.SimpleReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin {

    @Shadow @Final private MetadataSerializer rmMetadataSerializer;

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    private void openModelTextureContract(ResourceLocation location, CallbackInfoReturnable<Resource> callback) {
        Resource resource = PackTextureResources.open((ResourceManager) this, location, rmMetadataSerializer);
        if (resource != null) callback.setReturnValue(resource);
    }

    @Inject(method = "clearResources", at = @At("HEAD"))
    private void clearPackOrder(CallbackInfo ci) {
        ResourcePackOrder.clear((ResourceManager) this);
    }

    @Inject(method = "reloadResourcePack", at = @At("HEAD"))
    private void recordPackOrder(ResourcePack pack, CallbackInfo ci) {
        ResourcePackOrder.add((ResourceManager) this, pack);
    }
}
