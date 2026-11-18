package moddedmite.rustedironcore.mixin.block;

import huix.glacier.api.registry.sync.RegistryHelperImpl;
import huix.glacier.api.registry.sync.remappers.BlockRegistryRemapper;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.api.block.DynamicLightBlock;
import moddedmite.rustedironcore.internal.Bootstrap;
import net.minecraft.Block;
import net.minecraft.IBlockAccess;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumFace;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "tryPlaceFromHeldItem(IIILnet/minecraft/EnumFace;Lnet/minecraft/ItemStack;Lnet/minecraft/EntityPlayer;FFFZZZ)Z", at = @At("RETURN"))
    private void trackPlacedBlock(int x, int y, int z, EnumFace face, ItemStack stack, EntityPlayer player, float hitX, float hitY, float hitZ, boolean checkPlacement, boolean dropExisting, boolean testOnly, CallbackInfoReturnable<Boolean> callback) {
        if (!testOnly && callback.getReturnValueZ() && player.worldObj.getBlockId(x, y, z) == ((Block) (Object) this).blockID) {
            CriteriaTriggers.placedBlock(player, player.worldObj, x, y, z, stack);
        }
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        RegistryHelperImpl.registerRegistryRemapper(BlockRegistryRemapper::new);
        Bootstrap.logStage("block");
    }

    @Redirect(method = "getBlockBrightness", at = @At(value = "INVOKE", target = "Lnet/minecraft/IBlockAccess;getBrightness(IIII)F"))
    private float useDynamicBrightness(IBlockAccess access, int x, int y, int z, int staticEmission) {
        int emission = DynamicLightBlock.resolve(access, x, y, z, staticEmission);
        return access.getBrightness(x, y, z, emission);
    }

    @Redirect(method = "getMixedBrightnessForBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/IBlockAccess;getLightBrightnessForSkyBlocks(IIII)I"))
    private int useDynamicMixedBrightness(IBlockAccess access, int x, int y, int z, int staticEmission) {
        int emission = DynamicLightBlock.resolve(access, x, y, z, staticEmission);
        return access.getLightBrightnessForSkyBlocks(x, y, z, emission);
    }
}
