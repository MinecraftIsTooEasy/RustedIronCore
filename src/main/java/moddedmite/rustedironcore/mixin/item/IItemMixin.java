package moddedmite.rustedironcore.mixin.item;

import net.minecraft.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface IItemMixin {
    @Accessor("unlocalizedName")
    String ric$getUnlocalizedName();
}
