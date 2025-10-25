package moddedmite.rustedironcore.internal.config;

import moddedmite.rustedironcore.internal.unsafe.ExperimentalReleaseUtil;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinConfig implements IMixinConfigPlugin {
    @Override
    public void onLoad(String s) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("WorldProviderUnderworldMixin")) return RICConfig.WorldGenDelegate.get();
        if (mixinClassName.contains("mixin.dimension")) return RICConfig.UseCustomDimension.get();
        if (mixinClassName.contains("Packet")) return RICConfig.ApplyPacketPatches.get();
        if (mixinClassName.contains("GuiContainerMixin")) return ExperimentalReleaseUtil.isExperimental();
        if (mixinClassName.contains("GuiCraftingMixin")) return ExperimentalReleaseUtil.isExperimental();
        if (mixinClassName.contains("ItemBlockMixin")) return ExperimentalReleaseUtil.isExperimental();
        if (mixinClassName.contains("ClientPlayerMixin")) return ExperimentalReleaseUtil.isExperimental();
        return true;
    }

    @Override
    public void acceptTargets(Set<String> set, Set<String> set1) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }

    @Override
    public void postApply(String s, ClassNode classNode, String s1, IMixinInfo iMixinInfo) {

    }
}
