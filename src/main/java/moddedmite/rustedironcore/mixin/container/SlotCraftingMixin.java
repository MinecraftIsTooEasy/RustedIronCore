package moddedmite.rustedironcore.mixin.container;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.SlotCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlotCrafting.class)
public class SlotCraftingMixin {

    @Shadow
    private EntityPlayer thePlayer;

    @Inject(method = "onCrafting(Lnet/minecraft/ItemStack;)V", at = @At("HEAD"))
    private void addCraftingAchievements(ItemStack par1ItemStack, CallbackInfo ci) {
        Handlers.Achievement.onItemCrafted(this.thePlayer, par1ItemStack);
    }
}
