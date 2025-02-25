package moddedmite.rustedironcore.mixin.tileEntity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.Block;
import net.minecraft.ItemStack;
import net.minecraft.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TileEntityBeacon.class)
public class TileEntityBeaconMixin {
    @ModifyExpressionValue(method = "updateState", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;getBlockId(III)I"))
    private int handleMetals(int blockID) {
        boolean original = blockID == Block.blockEmerald.blockID || blockID == Block.blockDiamond.blockID || blockID == Block.blockCopper.blockID || blockID == Block.blockSilver.blockID || blockID == Block.blockGold.blockID || blockID == Block.blockIron.blockID || blockID == Block.blockMithril.blockID || blockID == Block.blockAdamantium.blockID;
        return Handlers.BeaconUpdate.onBlockValidModify((TileEntityBeacon) (Object) this, blockID, original) ? Block.blockEmerald.blockID : Block.stone.blockID;
    }

    @ModifyReturnValue(method = "isItemValidForSlot", at = @At("RETURN"))
    private boolean modifyValid(boolean original, @Local(argsOnly = true) ItemStack itemStack) {
        return Handlers.BeaconUpdate.onItemValidModify((TileEntityBeacon) (Object) this, itemStack, original);
    }
}
