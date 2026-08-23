package moddedmite.rustedironcore.mixin.block.entity;

import net.minecraft.Entity;
import net.minecraft.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(TileEntity.class)
public interface ITileEntityMixin {
    @Invoker
    static void invokeAddMapping(Class<? extends TileEntity> clazz, String string) {
        throw new AssertionError();
    }

    @Invoker
    static Map<Class<?>, String> invokeGetClassToNameMap() {
        throw new AssertionError();
    }
}
