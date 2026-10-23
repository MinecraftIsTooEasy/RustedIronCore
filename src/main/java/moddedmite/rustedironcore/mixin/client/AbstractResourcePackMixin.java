package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.api.resource.LegacyResourcePaths;
import moddedmite.rustedironcore.api.resource.ExactResourcePack;
import net.minecraft.AbstractResourcePack;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(AbstractResourcePack.class)
public abstract class AbstractResourcePackMixin implements ExactResourcePack {

    @Shadow protected abstract boolean hasResourceName(String name);

    @Shadow protected abstract InputStream getInputStreamByName(String name);

    @Override
    public boolean rustedIronCore$hasExactResource(String name) {
        return hasResourceName(name);
    }

    @Inject(method = "resourceExists", at = @At("HEAD"), cancellable = true)
    private void findAliasedResource(ResourceLocation location, CallbackInfoReturnable<Boolean> cir) {
        String root = "assets/" + location.getResourceDomain() + "/";
        cir.setReturnValue(LegacyResourcePaths.find(location.getResourceDomain(), location.getResourcePath(),
                path -> hasResourceName(root + path)) != null);
    }

    @Inject(method = "getInputStream", at = @At("HEAD"), cancellable = true)
    private void openAliasedResource(ResourceLocation location, CallbackInfoReturnable<InputStream> cir) {
        String root = "assets/" + location.getResourceDomain() + "/";
        String selected = LegacyResourcePaths.find(location.getResourceDomain(), location.getResourcePath(),
                path -> hasResourceName(root + path));
        if (selected != null) cir.setReturnValue(getInputStreamByName(root + selected));
    }
}
