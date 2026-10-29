package moddedmite.rustedironcore.internal.config;

import moddedmite.rustedironcore.api.util.Platform;

import java.util.List;

import static moddedmite.rustedironcore.internal.compat.FmlRuntime.Route.LEGACY;
import static moddedmite.rustedironcore.internal.compat.FmlRuntime.Route.NATIVE;

public class MixinConfig extends RoutedMixinPlugin {
    public MixinConfig() {
        super(List.of(
                new Routed("client.render.RenderBipedMixin", "special", LEGACY),
                new Routed("client.render.RenderPlayerMixin", "special", LEGACY),
                new Routed("client.FontRendererColorMixin", "text", LEGACY),
                new Routed("client.vulkan.FontRendererVulkanMixin", "text", NATIVE),
                new Routed("client.render.RenderGlobalMixin", "sky", LEGACY),
                new Routed("client.vulkan.RenderGlobalVulkanMixin", "sky", NATIVE),
                new Routed("client.render.TextureMapAtlasMixin", "texture", LEGACY),
                new Routed("client.vulkan.TextureMapVulkanMixin", "texture", NATIVE),
                new Routed("client.vulkan.TextureAtlasSpriteVulkanMixin", "texture", NATIVE)));
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("WorldProviderUnderworldMixin")) return RICConfig.WorldGenDelegate.get();
        if (mixinClassName.contains("mixin.dimension")) return RICConfig.UseCustomDimension.get();
        if (mixinClassName.contains("Packet")) return RICConfig.ApplyPacketPatches.get();
        if (mixinClassName.contains("GuiContainerMixin")) return Platform.isExperimental();
        if (mixinClassName.contains("GuiCraftingMixin")) return Platform.isExperimental();
        if (mixinClassName.contains("ItemBlockMixin")) return Platform.isExperimental();
        if (mixinClassName.equals("moddedmite.rustedironcore.mixin.client.player.ClientPlayerMixin")) {
            return Platform.isExperimental();
        }
        return true;
    }
}
