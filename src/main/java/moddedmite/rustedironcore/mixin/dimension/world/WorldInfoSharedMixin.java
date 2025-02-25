package moddedmite.rustedironcore.mixin.dimension.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.GameRules;
import net.minecraft.WorldClient;
import net.minecraft.WorldInfoShared;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(WorldInfoShared.class)
public class WorldInfoSharedMixin {
    @Shadow
    private long[] totalTime;

    @WrapOperation(method = {"<init>()V",
            "<init>(Lnet/minecraft/WorldSettings;Ljava/lang/String;)V",
            "<init>(Lnet/minecraft/NBTTagCompound;)V"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/WorldInfoShared;theGameRules:Lnet/minecraft/GameRules;", opcode = Opcodes.PUTFIELD))
    private void addDimension(WorldInfoShared instance, GameRules value, Operation<Void> original) {
        original.call(instance, value);
        this.totalTime = new long[Handlers.Dimension.getTotalDimensionsCount()];
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void setTotalWorldTimes(long[] total_world_times, WorldClient world) {
        for (int i = 0; i < Handlers.Dimension.getTotalDimensionsCount(); ++i) {
            this.totalTime[i] = total_world_times[i];
        }
        world.updateTickFlags();
    }
}
