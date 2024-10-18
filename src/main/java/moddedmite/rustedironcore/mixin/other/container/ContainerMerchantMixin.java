package moddedmite.rustedironcore.mixin.other.container;

import moddedmite.rustedironcore.network.Network;
import moddedmite.rustedironcore.network.packets.S2CSyncTradeRecipe;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerMerchant.class)
public abstract class ContainerMerchantMixin extends Container {

    @Shadow
    public abstract void detectAndSendChanges();

    @Shadow
    private IMerchant theMerchant;

    public ContainerMerchantMixin(EntityPlayer player) {
        super(player);
    }

    @Inject(method = "transferStackInSlot", at = @At("RETURN"))
    private void sync(EntityPlayer par1EntityPlayer, int par2, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player.onServer()) {
            ServerPlayer player = (ServerPlayer) this.player;
            player.sendContainerToPlayer(this);
            Network.sendToClient(player,new S2CSyncTradeRecipe(player.currentWindowId, this.theMerchant.getRecipes(player)));
        }
    }

    @Inject(method = "onContainerClosed", at = @At("RETURN"))
    private void sync(EntityPlayer par1EntityPlayer, CallbackInfo ci) {
        if (this.player.onServer()) {
            ((ServerPlayer) this.player).sendContainerToPlayer(this);
        }
    }

}
