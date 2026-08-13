package moddedmite.rustedironcore.mixin.client.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moddedmite.rustedironcore.internal.client.texture.InterpolatedAnimationMetadata;
import net.minecraft.AnimationMetadataSection;
import net.minecraft.AnimationMetadataSectionSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(AnimationMetadataSectionSerializer.class)
public abstract class AnimationMetadataSectionSerializerMixin {

    @Inject(method = "func_110493_a", at = @At("RETURN"))
    private void readInterpolate(JsonElement json, Type type, JsonDeserializationContext context, CallbackInfoReturnable<AnimationMetadataSection> cir) {
        JsonObject root = json.getAsJsonObject();
        boolean interpolate = root.has("interpolate") && root.get("interpolate").getAsBoolean();
        ((InterpolatedAnimationMetadata) cir.getReturnValue()).rustedIronCore$setInterpolateFrames(interpolate);
    }
}
