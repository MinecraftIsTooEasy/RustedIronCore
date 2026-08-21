package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ItemArrow;
import net.minecraft.Material;
import net.minecraft.RenderArrow;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RenderArrow.class)
public class RenderArrowMixin {

    @ModifyArg(method = "addTextures", at = @At(value = "INVOKE", target = "Lnet/minecraft/ResourceLocation;<init>(Ljava/lang/String;)V"), index = 0)
    private String applyRegisteredArrowDomain(String texture) {
        for (Material material : ItemArrow.material_types) {
            if (texture.endsWith('/' + material.name + ".png")) {
                return Handlers.ArrowRegister.resolveTexture(material, texture);
            }
        }
        return texture;
    }
}
