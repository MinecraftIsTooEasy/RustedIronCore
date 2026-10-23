package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(TextureMap.class)
public abstract class TextureMapMixin {

    @Inject(method = "registerIcons", at = @At("RETURN"))
    private void registerJsonModelTextures(CallbackInfo ci) {
        JsonBlockModelManager.INSTANCE.invalidateResourceCaches();
        Handlers.JsonModel.onJsonModelRegister();
        JsonBlockModelManager.INSTANCE.registerTextureAtlasSprites((TextureMap) (Object) this);
    }

    @Redirect(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource loadModernAtlasTexture(ResourceManager manager, ResourceLocation legacyLocation)
            throws IOException {
        return LegacyTextureResources.getResource(manager, legacyLocation);
    }
}
