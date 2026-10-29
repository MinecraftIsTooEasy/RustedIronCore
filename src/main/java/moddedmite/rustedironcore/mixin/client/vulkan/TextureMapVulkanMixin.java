package moddedmite.rustedironcore.mixin.client.vulkan;

import moddedmite.rustedironcore.internal.client.texture.LegacyAliasResourceManager;
import net.minecraft.ResourceManager;
import net.minecraft.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * FML 4.x native route of {@code TextureMapAtlasMixin}: FML's native {@code loadTextureAtlas} reads the sprites through
 * {@code AtlasPrefetch.fetch(int, ResourceManager, ResourceLocation)}, so the legacy name aliases are applied to the manager it gets.
 */
@Mixin(TextureMap.class)
public abstract class TextureMapVulkanMixin {

    @ModifyArg(method = "loadTextureAtlas", at = @At(value = "INVOKE", target = "Lnet/xiaoyu233/fml/vulkan/render/texture/AtlasPrefetch;fetch(ILnet/minecraft/ResourceManager;Lnet/minecraft/ResourceLocation;)V", remap = false), index = 1)
    private ResourceManager loadModernAtlasTextures(ResourceManager manager) {
        return new LegacyAliasResourceManager(manager);
    }
}
