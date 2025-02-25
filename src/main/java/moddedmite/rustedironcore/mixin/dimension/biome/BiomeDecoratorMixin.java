package moddedmite.rustedironcore.mixin.dimension.biome;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.BiomeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BiomeDecorator.class)
public class BiomeDecoratorMixin {
    @WrapOperation(method = "generateOres", at = @At(value = "INVOKE", target = "Lnet/minecraft/Minecraft;setErrorMessage(Ljava/lang/String;)V"))
    private void mute(String text, Operation<Void> original) {
    }
}
