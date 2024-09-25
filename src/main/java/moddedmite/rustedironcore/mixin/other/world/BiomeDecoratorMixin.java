package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.BiomeDecorator;
import net.minecraft.BiomeGenBase;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(BiomeGenBase par1BiomeGenBase, CallbackInfo ci) {
        Handlers.BiomeDecorate.onBiomeDecoratorInit((BiomeDecorator) (Object) this);
    }

    @Inject(method = "generateOres", at = @At("HEAD"))
    private void onOresGeneration(CallbackInfo ci) {
        Handlers.BiomeDecorate.onOresGeneration((BiomeDecorator) (Object) this);
    }

    @Inject(method = "decorate()V", at = @At(value = "FIELD", target = "Lnet/minecraft/BiomeDecorator;generateLakes:Z", opcode = Opcodes.GETFIELD))
    private void onDecorate(CallbackInfo ci) {
        Handlers.BiomeDecorate.onDecorate((BiomeDecorator) (Object) this);
    }
}
