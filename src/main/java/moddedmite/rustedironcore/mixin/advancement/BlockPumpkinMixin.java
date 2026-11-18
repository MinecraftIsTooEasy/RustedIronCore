package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.BlockPumpkin;
import net.minecraft.Entity;
import net.minecraft.ServerPlayer;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPumpkin.class)
public class BlockPumpkinMixin {

    @WrapOperation(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackSummoning(World world, Entity golem, Operation<Boolean> original) {
        boolean spawned = original.call(world, golem);
        if (spawned && !world.isRemote) for (Object entry : world.getEntitiesWithinAABB(ServerPlayer.class, golem.boundingBox.expand(5, 5, 5)))
            CriteriaTriggers.summonedEntity((ServerPlayer) entry, golem);
        return spawned;
    }
}
