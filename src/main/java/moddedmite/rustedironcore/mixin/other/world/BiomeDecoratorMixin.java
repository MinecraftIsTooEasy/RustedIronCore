package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.BiomeDecorationHandler;
import moddedmite.rustedironcore.api.event.handler.OreGenerationHandler;
import net.minecraft.BiomeDecorator;
import net.minecraft.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @Shadow
    protected World currentWorld;

    @Shadow
    protected int chunk_X;

    @Shadow
    protected int chunk_Z;

    @Shadow
    protected Random randomGenerator;

    @Inject(method = "generateOres", at = @At("HEAD"))
    private void onOresGeneration(CallbackInfo ci) {
        Handlers.OreGeneration.onOresGeneration(
                OreGenerationHandler.context(
                        (BiomeDecorator) (Object) this, this.currentWorld));
    }

    @Inject(method = "decorate()V", at = @At(value = "FIELD", target = "Lnet/minecraft/BiomeDecorator;generateLakes:Z", opcode = Opcodes.GETFIELD))
    private void onDecorate(CallbackInfo ci) {
        Handlers.BiomeDecoration.onDecorate(
                BiomeDecorationHandler.context(
                        this.currentWorld, this.randomGenerator,
                        this.chunk_X, this.chunk_Z));
    }
}
