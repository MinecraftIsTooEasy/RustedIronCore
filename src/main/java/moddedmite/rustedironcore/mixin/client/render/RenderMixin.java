package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.render.RenderAPI;
import net.minecraft.Minecraft;
import net.minecraft.Render;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Render.class)
public class RenderMixin implements RenderAPI {
    @Shadow
    protected ResourceLocation[] textures;

    @Shadow
    protected ResourceLocation[] textures_glowing;

    public boolean forceGlowOverride() {
        return false;
    }

    @Overwrite
    protected void setTexture(int index, String path, String glow_path) {
        if (this.textures[index] != null) {
            Minecraft.setErrorMessage("setTexture: texture [" + index + "] has already been set for " + this);
        } else {
            ResourceLocation texture = new ResourceLocation(path + ".png");
            this.textures[index] = texture;
            ResourceLocation texture_glowing = new ResourceLocation(glow_path + "_glow.png", false);
            if ((Minecraft.MITE_resource_pack != null && Minecraft.MITE_resource_pack.resourceExists(texture_glowing)) || this.forceGlowOverride() ) {
                this.textures_glowing[index] = texture_glowing;
            }

        }
    }

//    @Inject(method = "setTexture(ILjava/lang/String;Ljava/lang/String;)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/ResourceLocation;<init>(Ljava/lang/String;Z)V"))
//    private void captureGlowingAndSet(int index, String path, String glow_path, CallbackInfo ci, @Local(ordinal = 0, name = "texture_glowing") ResourceLocation texture_glowing){
//        if (this.forceGlowOverride()) {
//            this.textures_glowing[index] = texture_glowing;
//        }
//    }
}
