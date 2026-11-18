package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.advancement.AdvancementActor;
import moddedmite.rustedironcore.internal.advancement.AdvancementBreeding;
import net.minecraft.Entity;
import net.minecraft.EntityAnimal;
import net.minecraft.NBTTagCompound;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityAnimal.class)
public class EntityAnimalMixin implements AdvancementActor {

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
    private void writeBreeder(NBTTagCompound data, CallbackInfo callback) {
        if (!ric$advancementActor.isEmpty()) data.setString("ric:breeder", ric$advancementActor);
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readBreeder(NBTTagCompound data, CallbackInfo callback) {
        ric$advancementActor = data.getString("ric:breeder");
    }

    @Inject(method = "onLivingUpdate", at = @At("HEAD"))
    private void clearExpiredBreeder(CallbackInfo callback) {
        if (!((EntityAnimal) (Object) this).isInLove()) ric$advancementActor = "";
    }

    @WrapOperation(method = "procreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackBreeding(World world, Entity child, Operation<Boolean> original, EntityAnimal partner) {
        boolean spawned = original.call(world, child);
        if (spawned) AdvancementBreeding.born((EntityAnimal) (Object) this, partner, child);
        return spawned;
    }
}
