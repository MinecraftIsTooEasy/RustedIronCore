package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.internal.advancement.AdvancementLightning;
import net.minecraft.AxisAlignedBB;
import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.EntityLightningBolt;
import net.minecraft.ServerPlayer;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(EntityLightningBolt.class)
public class EntityLightningBoltMixin implements AdvancementLightning {

    @Unique private final Set<Entity> ric$struckEntities = new HashSet<>();
    @Unique private boolean ric$reported;
    @Unique private int ric$blocksSetOnFire;

    @Override
    public int ric$getBlocksSetOnFire() {
        return ric$blocksSetOnFire;
    }

    @Override
    public Set<Entity> ric$getStruckEntities() {
        return Set.copyOf(ric$struckEntities);
    }

    @WrapOperation(method = {"<init>", "onUpdate"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/World;setBlock(IIII)Z"))
    private boolean trackIgnitedBlock(World world, int x, int y, int z, int id, Operation<Boolean> original) {
        boolean placed = original.call(world, x, y, z, id);
        if (placed && id == Block.fire.blockID) ric$blocksSetOnFire++;
        return placed;
    }

    @WrapOperation(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/Entity;onStruckByLightning(Lnet/minecraft/EntityLightningBolt;)V"))
    private void trackStruckEntity(Entity entity, EntityLightningBolt bolt, Operation<Void> original) {
        original.call(entity, bolt);
        ric$struckEntities.add(entity);
    }

    @Inject(method = "onUpdate", at = @At("RETURN"))
    private void trackLightning(CallbackInfo callback) {
        EntityLightningBolt bolt = (EntityLightningBolt) (Object) this;
        if (!bolt.isDead || bolt.worldObj.isRemote || ric$reported) return;
        ric$reported = true;
        List<Entity> bystanders = new ArrayList<>();
        AxisAlignedBB area = AxisAlignedBB.getAABBPool().getAABB(bolt.posX - 15, bolt.posY - 15, bolt.posZ - 15, bolt.posX + 15, bolt.posY + 21, bolt.posZ + 15);
        for (Object raw : bolt.worldObj.getEntitiesWithinAABBExcludingEntity(bolt, area)) {
            Entity entity = (Entity) raw;
            if (entity.isEntityAlive() && !ric$struckEntities.contains(entity)) bystanders.add(entity);
        }
        for (Object raw : bolt.worldObj.playerEntities) if (raw instanceof ServerPlayer player && player.getDistanceSqToEntity(bolt) < 256 * 256)
            CriteriaTriggers.lightningStrike(player, bolt, bystanders);
    }
}
