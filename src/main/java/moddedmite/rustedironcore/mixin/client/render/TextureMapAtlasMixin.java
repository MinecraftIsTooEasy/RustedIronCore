package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

/** Legacy render route: atlas sprites are read in MITE's {@code loadTextureAtlas}. */
@Mixin(TextureMap.class)
public abstract class TextureMapAtlasMixin {

    @Redirect(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource loadModernAtlasTexture(ResourceManager manager, ResourceLocation legacyLocation)
            throws IOException {
        return LegacyTextureResources.getResource(manager, legacyLocation);
    }
}
