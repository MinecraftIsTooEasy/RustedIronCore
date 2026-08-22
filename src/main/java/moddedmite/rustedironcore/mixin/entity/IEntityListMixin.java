package moddedmite.rustedironcore.mixin.entity;

import net.minecraft.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityList.class)
public interface IEntityListMixin {
    @Invoker
    static void invokeAddMapping(Class<?> clazz, String name, int id) {
        throw new AssertionError();
    }
}
