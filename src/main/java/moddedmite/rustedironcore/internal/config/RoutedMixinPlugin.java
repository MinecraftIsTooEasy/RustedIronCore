package moddedmite.rustedironcore.internal.config;

import moddedmite.rustedironcore.internal.compat.FmlRuntime;
import net.xiaoyu233.fml.FishModLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Config plugin that contributes the client mixins of the render route the running FishModLoader needs (see {@link FmlRuntime}).
 * <p>
 * Routed mixins are never listed in the JSON, only here, so exactly one variant is loaded:
 * <ul>
 * <li>a mixin whose injection points only exist in MITE's original method body has a legacy variant and a native variant;</li>
 * <li>a mixin that {@code @Overwrite}s a method FML 4.x's native renderer {@code @Redirect}s is listed by the normal config on the
 * legacy route and by a {@code *.vulkan.mixins.json} config on the native route. That config has a lower {@code mixinPriority} than
 * FML's native configs (500) so the overwrite merges first and the native redirects can still apply to it.</li>
 * </ul>
 */
public class RoutedMixinPlugin implements IMixinConfigPlugin {
    /** {@code mixin} (relative to the config package) is applied when the FML native area {@code area} takes {@code route}. */
    public record Routed(String mixin, String area, FmlRuntime.Route route) {
    }

    private final List<Routed> clientMixins;

    protected RoutedMixinPlugin(List<Routed> clientMixins) {
        this.clientMixins = clientMixins;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        if (this.clientMixins.isEmpty() || FishModLoader.isServer()) {
            return null;
        }
        List<String> mixins = new ArrayList<>();
        for (Routed routed : this.clientMixins) {
            if (FmlRuntime.route(routed.area()) == routed.route()) mixins.add(routed.mixin());
        }
        return mixins;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
