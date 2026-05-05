package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import net.minecraft.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureMap.class)
public abstract class TextureMapMixin {

    @Inject(method = "registerIcons", at = @At("RETURN"))
    private void registerJsonModelTextures(CallbackInfo ci) {
        Handlers.JsonModel.onJsonModelRegister();
        JsonBlockModelManager.INSTANCE.registerTextureAtlasSprites((TextureMap) (Object) this);
    }
}
