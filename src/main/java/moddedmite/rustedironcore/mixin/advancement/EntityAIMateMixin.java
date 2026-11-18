package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.advancement.AdvancementBreeding;
import net.minecraft.Entity;
import net.minecraft.EntityAIMate;
import net.minecraft.EntityAgeable;
import net.minecraft.EntityAnimal;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityAIMate.class)
public class EntityAIMateMixin {

    @Shadow private EntityAnimal theAnimal;
    @Shadow private EntityAnimal targetMate;

    @WrapOperation(method = "spawnBaby", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;spawnEntityInWorld(Lnet/minecraft/Entity;)Z"))
    private boolean trackBreeding(World world, Entity entity, Operation<Boolean> original) {
        boolean spawned = original.call(world, entity);
        if (spawned && entity instanceof EntityAgeable) AdvancementBreeding.born(theAnimal, targetMate, entity);
        return spawned;
    }
}
