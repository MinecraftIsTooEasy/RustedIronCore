package moddedmite.rustedironcore.internal.config;

import java.util.List;

import static moddedmite.rustedironcore.internal.compat.FmlRuntime.Route.NATIVE;

/** Plugin of {@code glacier.vulkan.mixins.json}: overwrites that must merge before FML 4.x's native redirects. */
public class GlacierVulkanMixinConfig extends RoutedMixinPlugin {
    public GlacierVulkanMixinConfig() {
        super(List.of(new Routed("client.gui.GuiContainerCreativeMixin", "container", NATIVE)));
    }
}
