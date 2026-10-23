package moddedmite.rustedironcore.mixin.client.audio;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.api.sound.ResourceSoundPlayer;
import net.minecraft.SoundManager;
import net.minecraft.SoundPool;
import net.minecraft.SoundPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @WrapOperation(method = "playSoundFX(Ljava/lang/String;FF)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/SoundPool;getRandomSoundFromSoundPool(Ljava/lang/String;)Lnet/minecraft/SoundPoolEntry;"))
    private SoundPoolEntry selectResourceSound(SoundPool pool, String name, Operation<SoundPoolEntry> original) {
        SoundPoolEntry resource = ResourceSoundPlayer.lookup(name);
        return resource == null ? original.call(pool, name) : resource;
    }
}
