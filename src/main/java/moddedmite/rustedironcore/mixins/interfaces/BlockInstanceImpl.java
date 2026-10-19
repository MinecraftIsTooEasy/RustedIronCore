package moddedmite.rustedironcore.mixins.interfaces;

import moddedmite.rustedironcore.core.Holder;
import moddedmite.rustedironcore.core.TypedInstance;
import net.minecraft.Block;

public interface BlockInstanceImpl extends TypedInstance<Block> {
    Holder<Block> ric$typeHolder();

    @Override
    default Holder<Block> typeHolder() {
        return this.ric$typeHolder();
    }
}
