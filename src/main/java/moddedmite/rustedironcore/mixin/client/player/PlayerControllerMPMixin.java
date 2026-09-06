package moddedmite.rustedironcore.mixin.client.player;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.sound.BlockSoundType;
import net.minecraft.PlayerControllerMP;
import net.minecraft.StepSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerControllerMP.class)
public abstract class PlayerControllerMPMixin {

    @WrapOperation(method = "onPlayerDamageBlock(IIILnet/minecraft/EnumFace;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/StepSound;getStepSound()Ljava/lang/String;"))
    private String rusted_iron_core$useBlockHitSound(StepSound instance, Operation<String> original) {
        String originalSound = original.call(instance);
        return instance instanceof BlockSoundType soundType ? soundType.getHitSound() : originalSound;
    }
}
