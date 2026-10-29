package moddedmite.rustedironcore.internal.config;

import java.util.List;

import static moddedmite.rustedironcore.internal.compat.FmlRuntime.Route.LEGACY;

/** Plugin of {@code glacier.mixins.json}. */
public class GlacierMixinConfig extends RoutedMixinPlugin {
    public GlacierMixinConfig() {
        super(List.of(new Routed("client.gui.GuiContainerCreativeMixin", "container", LEGACY)));
    }
}
