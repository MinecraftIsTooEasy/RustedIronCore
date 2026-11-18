package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.Block;
import net.minecraft.BlockBreakInfo;
import net.minecraft.EntityItem;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Block.class)
public interface BlockAccessor {

    @Invoker("getTextureName")
    String invokeGetTextureName();

    @Invoker("dropBlockAsItem_do")
    EntityItem rusted_iron_core$dropBlockAsItem(BlockBreakInfo info, ItemStack stack);
}
