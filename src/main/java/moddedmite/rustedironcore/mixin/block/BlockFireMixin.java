package moddedmite.rustedironcore.mixin.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BlockFire;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockFire.class)
public class BlockFireMixin {
    @WrapOperation(method = "tryExtinguishByItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;getItem()Lnet/minecraft/Item;"))
    private Item redirectSoICanExtinguishFire(ItemStack instance, Operation<Item> original) {
        return Handlers.Barbecue.getCookResult(instance).isPresent() ? Item.porkRaw : original.call(instance);
    }
}
