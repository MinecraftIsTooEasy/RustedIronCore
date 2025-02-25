package moddedmite.rustedironcore.mixin.container;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotFurnace.class)
public abstract class SlotFurnaceMixin extends SlotCraftingBase {
    public SlotFurnaceMixin(EntityPlayer player, IInventory inventory, int slot_index, int display_x, int display_y) {
        super(player, inventory, slot_index, display_x, display_y);
    }

    @Inject(method = "onCrafting", at = @At("TAIL"))
    private void itfAchievementCheck(ItemStack item_stack, CallbackInfo ci) {
        Handlers.Achievement.onItemSmelt(this.player, item_stack);
    }
}
