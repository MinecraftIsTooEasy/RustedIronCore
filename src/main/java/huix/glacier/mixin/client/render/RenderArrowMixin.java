package huix.glacier.mixin.client.render;

import huix.glacier.util.GlacierHelper;
import net.minecraft.RenderArrow;
import net.minecraft.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin( RenderArrow.class )
public class RenderArrowMixin {

    @Shadow
    private static ResourceLocation[] textures;

    @Overwrite
    public void addTextures() {
        textures = new ResourceLocation[GlacierHelper.arrow_material_types.size()];

        for(int i = 0; i < textures.length; ++i) {
            textures[i] = new ResourceLocation("textures/entity/arrows/" + GlacierHelper.arrow_material_types.get(i).name + ".png");
        }

    }
}
