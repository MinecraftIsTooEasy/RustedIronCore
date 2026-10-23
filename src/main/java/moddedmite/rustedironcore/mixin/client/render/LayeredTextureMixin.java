package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.minecraft.LayeredTexture;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;

@Mixin(LayeredTexture.class)
public class LayeredTextureMixin {

    @Redirect(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource loadModernLayer(ResourceManager manager, ResourceLocation location) throws IOException {
        return LegacyTextureResources.getResource(manager, location);
    }
}
