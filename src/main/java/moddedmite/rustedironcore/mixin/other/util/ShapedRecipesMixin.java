package moddedmite.rustedironcore.mixin.other.util;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;
import moddedmite.rustedironcore.api.interfaces.IRecipeExtend;
import net.minecraft.CraftingResult;
import net.minecraft.InventoryCrafting;
import net.minecraft.ItemStack;
import net.minecraft.ShapedRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(ShapedRecipes.class)
public class ShapedRecipesMixin implements IRecipeExtend {
    @Unique
    private boolean allowDamaged;

    @Unique
    private boolean keepQuality;

    @Unique
    private List<CraftingRecipeRegisterEvent.ConsumeRule> consumeOverrides;

    @ModifyReturnValue(method = "getCraftingResult", at = @At("RETURN"))
    private CraftingResult onReturn(CraftingResult original) {
        if (this.allowDamaged) original.setRepair();// to enable crafting and avoid wrong tooltip
        if (this.keepQuality) original.setExperienceCostExempt();
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
    public void ric$SetConsumeRules(List<CraftingRecipeRegisterEvent.ConsumeRule> list) {
        this.consumeOverrides = list;
    }

    @Override
    public List<CraftingRecipeRegisterEvent.ConsumeRule> ric$GetConsumeRules() {
        return this.consumeOverrides;
    }

    @Override
    public void ric$SetKeepQuality() {
        this.keepQuality = true;
    }

    @ModifyReturnValue(method = "getCraftingResult", at = @At("RETURN"))
    private CraftingResult modifyResult(CraftingResult original, @Local(argsOnly = true) InventoryCrafting par1InventoryCrafting) {
        if (this.keepQuality) {
            for (int var3 = 0; var3 < par1InventoryCrafting.getSizeInventory(); ++var3) {
                ItemStack var4 = par1InventoryCrafting.getStackInSlot(var3);
                if (var4 == null || !var4.getItem().hasQuality()) continue;
                original.item_stack.setQuality(var4.getQuality());
            }
        }
        return original;
    }
}
