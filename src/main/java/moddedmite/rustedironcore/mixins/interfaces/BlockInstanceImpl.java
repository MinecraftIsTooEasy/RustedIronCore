package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.TypedInstance;
import net.minecraft.Block;

public interface BlockInstanceImpl {
    Holder<Block> ric$typeHolder();

    @SuppressWarnings({"RedundantCast", "Convert2Lambda", "Anonymous2MethodRef"})
    static TypedInstance<Block> cast(Block block) {
        BlockInstanceImpl instance = (BlockInstanceImpl) (Object) block;
        return new TypedInstance<>() {
            @Override
            public Holder<Block> typeHolder() {
                return instance.ric$typeHolder();
            }
        };
    }
}
