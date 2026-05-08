package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.RenderBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderBlocks.class)
public interface RenderBlocksAccessor {

    @Accessor("renderAllFaces")
    boolean rustedIronCore$getRenderAllFaces();
}
