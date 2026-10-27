package moddedmite.rustedironcore.mixin.item;

import huix.glacier.api.extension.item.IRockItem;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.property.ItemProperties;
import net.minecraft.Item;
import net.minecraft.ItemRock;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRock.class)
public class ItemRockMixin {
    @Inject(method = "getExperienceValueWhenSacrificed", at = @At("HEAD"), cancellable = true)
    private static void applySacrificeRules(ItemStack item_stack, CallbackInfoReturnable<Integer> cir) {
        if (!Handlers.Sacrifice.canSacrifice(item_stack)) {
            cir.setReturnValue(0);
            return;
        }
        Item item = item_stack.getItem();
        if (item instanceof IRockItem iRockItem) {
            cir.setReturnValue(iRockItem.getExperienceValueWhenSacrificed());
            return;
        }
        ItemProperties.RockExperience.getOptional(item).ifPresent(cir::setReturnValue);
    }
}
