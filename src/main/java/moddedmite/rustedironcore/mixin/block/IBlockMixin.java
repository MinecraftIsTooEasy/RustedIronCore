package moddedmite.rustedironcore.mixin.block;

import net.minecraft.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Block.class)
public interface IBlockMixin {
    @Accessor("unlocalizedName")
    String ric$getUnlocalizedName();
}
