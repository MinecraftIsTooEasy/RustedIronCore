package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.EntityLivingBase;
import net.minecraft.ItemDamageResult;
import net.minecraft.ItemStack;
import net.minecraft.ServerPlayer;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @WrapOperation(method = "tryDamageItem(Lnet/minecraft/DamageSource;ILnet/minecraft/EntityLivingBase;)Lnet/minecraft/ItemDamageResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;tryDamageItem(Lnet/minecraft/World;IZ)Lnet/minecraft/ItemDamageResult;"))
    private ItemDamageResult trackDurability(ItemStack stack, World world, int damage, boolean preventDestruction, Operation<ItemDamageResult> original, @Local(argsOnly = true) EntityLivingBase owner) {
        int before = stack.getItemDamage();
        ItemDamageResult result = original.call(stack, world, damage, preventDestruction);
        if (owner instanceof ServerPlayer player && before != stack.getItemDamage()) CriteriaTriggers.durabilityChanged(player, stack, before);
        return result;
    }
}
