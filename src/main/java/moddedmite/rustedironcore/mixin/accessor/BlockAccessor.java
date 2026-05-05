package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {

    @Invoker("getTextureName")
    String invokeGetTextureName();
}
