package moddedmite.rustedironcore.mixin.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.block.Bed;
import net.minecraft.BlockBed;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBed.class)
public class BlockBedMixin implements Bed {

    @ModifyExpressionValue(method = "getNearestEmptyChunkCoordinates(Lnet/minecraft/World;IIIILnet/minecraft/Vec3;)Lnet/minecraft/ChunkCoordinates;", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockBed;j(I)I"))
    private static int resolveBedDirection(int original, @Local(argsOnly = true) World world, @Local(argsOnly = true, ordinal = 0) int x, @Local(argsOnly = true, ordinal = 1) int y, @Local(argsOnly = true, ordinal = 2) int z) {
        Bed bed = Bed.getBed(world, x, y, z);
        return bed == null ? original : bed.getBedDirection(world, x, y, z);
    }
}
