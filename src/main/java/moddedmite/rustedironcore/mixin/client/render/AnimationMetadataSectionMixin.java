package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.internal.client.texture.InterpolatedAnimationMetadata;
import net.minecraft.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AnimationMetadataSection.class)
public abstract class AnimationMetadataSectionMixin implements InterpolatedAnimationMetadata {

    @Unique
    private boolean rustedIronCore$interpolateFrames;

    @Override
    public boolean rustedIronCore$interpolateFrames() {
        return this.rustedIronCore$interpolateFrames;
    }

    @Override
    public void rustedIronCore$setInterpolateFrames(boolean interpolateFrames) {
        this.rustedIronCore$interpolateFrames = interpolateFrames;
    }
}
