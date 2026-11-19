package moddedmite.rustedironcore.internal.config;

import java.util.List;

import static moddedmite.rustedironcore.internal.compat.FmlRuntime.Route.NATIVE;

/** Plugin of {@code rustedironcore.vulkan.mixins.json}: overwrites that must merge before FML 4.x's native redirects. */
public class VulkanMixinConfig extends RoutedMixinPlugin {
    public VulkanMixinConfig() {
        super(List.of(
                new Routed("client.render.RenderBipedMixin", "special", NATIVE),
                new Routed("client.render.RenderPlayerMixin", "special", NATIVE)));
    }
}
