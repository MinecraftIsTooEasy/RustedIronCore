package moddedmite.rustedironcore.mixin.other.util;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import moddedmite.rustedironcore.api.interfaces.IRecipeExtend;
import net.minecraft.CraftingResult;
import net.minecraft.ItemStack;
import net.minecraft.ShapedRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.UnaryOperator;

@Mixin(ShapedRecipes.class)
public class ShapedRecipesMixin implements IRecipeExtend {
    @Unique
    private boolean allowDamaged;

    @Unique
    private List<UnaryOperator<ItemStack>> consumeOverrides;

    @ModifyReturnValue(method = "getCraftingResult", at = @At("RETURN"))
    private CraftingResult onReturn(CraftingResult original) {
        if (this.allowDamaged) original.setRepair();
        return original;
    }

    @Override
    public void ric$SetAllowDamaged(boolean b) {
        this.allowDamaged = b;
    }

    @Override
    public boolean ric$AllowDamaged() {
        return this.allowDamaged;
    }

    @Override
    public void ric$SetConsumeOverride(List<UnaryOperator<ItemStack>> list) {
        this.consumeOverrides = list;
    }

    @Override
    public List<UnaryOperator<ItemStack>> ric$GetConsumeOverride() {
        return this.consumeOverrides;
    }
}
