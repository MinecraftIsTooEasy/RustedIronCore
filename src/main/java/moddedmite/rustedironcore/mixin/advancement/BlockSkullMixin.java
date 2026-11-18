package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.BlockSkull;
import net.minecraft.Entity;
import net.minecraft.ServerPlayer;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockSkull.class)
public class BlockSkullMixin {

    @WrapOperation(method = "makeWither", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackSummoning(World world, Entity wither, Operation<Boolean> original) {
        boolean spawned = original.call(world, wither);
        if (spawned && !world.isRemote) for (Object entry : world.getEntitiesWithinAABB(ServerPlayer.class, wither.boundingBox.expand(50, 50, 50)))
            CriteriaTriggers.summonedEntity((ServerPlayer) entry, wither);
        return spawned;
    }
}
