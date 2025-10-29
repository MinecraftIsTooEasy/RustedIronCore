package moddedmite.rustedironcore.mixin.util;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;
import moddedmite.rustedironcore.api.util.Platform;
import moddedmite.rustedironcore.internal.event.Hooks;
import moddedmite.rustedironcore.internal.unsafe.CraftingResultAccess;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Comparator;
import java.util.List;

@Mixin(CraftingManager.class)
public abstract class CraftingManagerMixin {
    @Shadow
    public abstract ShapedRecipes addRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);

    @Shadow
    public abstract ShapelessRecipes addShapelessRecipe(ItemStack par1ItemStack, boolean include_in_lowest_crafting_difficulty_determination, Object... par2ArrayOfObj);

    @Shadow
    private List recipes;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Collections;sort(Ljava/util/List;Ljava/util/Comparator;)V"))
    private <T> void postRecipes(List<T> list, Comparator<? super T> c, Operation<Void> original) {
        CraftingRecipeRegisterEvent craftingRecipeRegisterEvent = new CraftingRecipeRegisterEvent(this.recipes);
        Handlers.Crafting.publish(craftingRecipeRegisterEvent);
        for (CraftingRecipeRegisterEvent.RecipeArgs shaped : craftingRecipeRegisterEvent.getShaped()) {
            ShapedRecipes shapedRecipes = this.addRecipe(shaped.result, shaped.include_in_lowest_crafting_difficulty_determination, shaped.inputs);
            shaped.modifyShapedRecipe(shapedRecipes);
        }
        for (CraftingRecipeRegisterEvent.RecipeArgs shapeless : craftingRecipeRegisterEvent.getShapeless()) {
            ShapelessRecipes shapelessRecipes = this.addShapelessRecipe(shapeless.result, shapeless.include_in_lowest_crafting_difficulty_determination, shapeless.inputs);
            shapeless.modifyShapelessRecipe(shapelessRecipes);
        }
        original.call(list, c);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        Hooks.postCraftingRecipeRegister();
    }

    @Inject(method = "findMatchingRecipe", at = @At(value = "INVOKE", target = "Lnet/minecraft/InventoryCrafting;getEventHandler()Lnet/minecraft/Container;"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void repairArmor(InventoryCrafting par1InventoryCrafting, World par2World, EntityPlayer player, CallbackInfoReturnable<CraftingResult> cir, int var3, ItemStack var4, ItemStack var5, int var6) {
        if (var3 != 2 || var4 == null || var5 == null) return;
        if (!Platform.isExperimental()) return;
        CraftingResult match = Handlers.Crafting.repairArmor(player, var4, var5);
        if (match == null) {
            cir.setReturnValue(null);
            return;
        }
        if (CraftingResultAccess.isRepair(match)) {
            cir.setReturnValue(match);
        }
    }
}
