package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.internal.advancement.AdvancementActor;
import net.minecraft.Entity;
import net.minecraft.EntityZombie;
import net.minecraft.NBTTagCompound;
import net.minecraft.ServerPlayer;
import net.minecraft.World;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityZombie.class)
public class EntityZombieMixin implements AdvancementActor {

    @Unique private String ric$advancementActor = "";

    @Override
    public String ric$getAdvancementActor() {
        return ric$advancementActor;
    }

    @Override
    public void ric$setAdvancementActor(String name) {
        ric$advancementActor = name;
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void writeCuringPlayer(NBTTagCompound data, CallbackInfo callback) {
        if (!ric$advancementActor.isEmpty()) data.setString("ric:curing_player", ric$advancementActor);
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readCuringPlayer(NBTTagCompound data, CallbackInfo callback) {
        ric$advancementActor = data.getString("ric:curing_player");
    }

    @WrapOperation(method = "convertToVillager", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackCuring(World world, Entity villager, Operation<Boolean> original) {
        boolean spawned = original.call(world, villager);
        if (spawned && !world.isRemote && !ric$advancementActor.isEmpty()) {
            ServerPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(ric$advancementActor);
            if (player != null) CriteriaTriggers.curedZombieVillager(player, (EntityZombie) (Object) this, villager);
        }
        return spawned;
    }
}
