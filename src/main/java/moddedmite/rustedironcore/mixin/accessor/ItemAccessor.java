package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Item.class)
public interface ItemAccessor {

    @Invoker("getIconString")
    String invokeGetIconString();
}
