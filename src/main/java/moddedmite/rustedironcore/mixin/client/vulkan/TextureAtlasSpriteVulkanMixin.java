package moddedmite.rustedironcore.mixin.client.vulkan;

import moddedmite.rustedironcore.internal.client.texture.InterpolatedSprite;
import net.minecraft.AnimationMetadataSection;
import net.minecraft.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FML 4.x native route of {@code TextureAtlasSpriteMixin.retainInterpolateMetadata}: atlas sprites are decoded on FML's worker pool
 * and loaded with {@code fml$loadDecoded} (added by FML's native texture mixin) instead of {@code loadSprite}.
 */
@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteVulkanMixin {

    @Inject(method = "fml$loadDecoded(II[ILnet/minecraft/AnimationMetadataSection;)V", at = @At("RETURN"), remap = false)
    private void retainInterpolateMetadata(int imageWidth, int imageHeight, int[] pixels, AnimationMetadataSection animation, CallbackInfo ci) {
        ((InterpolatedSprite) this).rustedIronCore$retainInterpolation(animation);
    }
}
