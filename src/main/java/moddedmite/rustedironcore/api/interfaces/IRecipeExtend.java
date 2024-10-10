package moddedmite.rustedironcore.api.interfaces;

import net.minecraft.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.UnaryOperator;

public interface IRecipeExtend {
    void ric$SetAllowDamaged(boolean b);

    boolean ric$AllowDamaged();

    void ric$SetConsumeOverride(List<UnaryOperator<ItemStack>> list);

    @Nullable
    List<UnaryOperator<ItemStack>> ric$GetConsumeOverride();
}
