package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.FileNotFoundException;

@Mixin(TextureMap.class)
public abstract class TextureMapMixin {

    @Inject(method = "registerIcons", at = @At("RETURN"))
    private void registerJsonModelTextures(CallbackInfo ci) {
        Handlers.JsonModel.onJsonModelRegister();
        JsonBlockModelManager.INSTANCE.registerTextureAtlasSprites((TextureMap) (Object) this);
    }

    @Redirect(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource ric$loadModernAtlasTexture(ResourceManager manager, ResourceLocation legacyLocation)
            throws IOException {
        try {
            return manager.getResource(legacyLocation);
        } catch (Exception legacyLookupFailure) {
            ResourceLocation modernLocation = ric$toModernAtlasLocation(legacyLocation);
            if (modernLocation == null) {
                throw ric$missingAtlasTexture(legacyLocation, legacyLookupFailure);
            }

            try {
                return manager.getResource(modernLocation);
            } catch (Exception modernLookupFailure) {
                throw ric$missingAtlasTexture(modernLocation, legacyLookupFailure, modernLookupFailure);
            }
        }
    }

    @Unique
    private static FileNotFoundException ric$missingAtlasTexture(ResourceLocation location, Exception... lookupFailures) {
        FileNotFoundException missingResource = new FileNotFoundException(location.toString());
        for (Exception lookupFailure : lookupFailures) {
            missingResource.addSuppressed(lookupFailure);
        }
        return missingResource;
    }

    @Unique
    private static ResourceLocation ric$toModernAtlasLocation(ResourceLocation legacyLocation) {
        String path = legacyLocation.getResourcePath();
        String modernPath;
        if (path.startsWith("textures/blocks/")) {
            modernPath = "textures/block/" + path.substring("textures/blocks/".length());
        } else if (path.startsWith("textures/items/")) {
            modernPath = "textures/item/" + path.substring("textures/items/".length());
        } else {
            return null;
        }
        return new ResourceLocation(legacyLocation.getResourceDomain(), modernPath, false);
    }
}
