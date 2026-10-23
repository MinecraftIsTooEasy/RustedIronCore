package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.resource.LegacyHudSprites;
import moddedmite.rustedironcore.api.resource.LegacyTextureResources;
import net.minecraft.Resource;
import net.minecraft.ResourceLocation;
import net.minecraft.ResourceManager;
import net.minecraft.SimpleTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Mixin(SimpleTexture.class)
public class SimpleTextureMixin {

    @Shadow
    @Final
    private ResourceLocation textureLocation;

    @Redirect(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/ResourceManager;getResource(Lnet/minecraft/ResourceLocation;)Lnet/minecraft/Resource;"))
    private Resource loadModernTexture(ResourceManager manager, ResourceLocation location) throws IOException {
        return LegacyTextureResources.getResource(manager, location);
    }

    @Redirect(method = "loadTexture", at = @At(value = "INVOKE",
            target = "Ljavax/imageio/ImageIO;read(Ljava/io/InputStream;)Ljava/awt/image/BufferedImage;"))
    private BufferedImage loadHudSprites(InputStream stream, ResourceManager manager) throws IOException {
        return LegacyHudSprites.load(manager, textureLocation, ImageIO.read(stream));
    }
}
