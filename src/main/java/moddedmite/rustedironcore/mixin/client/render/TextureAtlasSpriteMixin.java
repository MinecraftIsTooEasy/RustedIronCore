package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.internal.client.texture.InterpolatedAnimationMetadata;
import net.minecraft.AnimationMetadataSection;
import net.minecraft.Resource;
import net.minecraft.TextureAtlasSprite;
import net.minecraft.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TextureAtlasSprite.class)
public abstract class TextureAtlasSpriteMixin {

    @Shadow
    protected List<int[]> framesTextureData;
    @Shadow
    private AnimationMetadataSection animationMetadata;
    @Shadow
    protected int originX;
    @Shadow
    protected int originY;
    @Shadow
    protected int width;
    @Shadow
    protected int height;
    @Shadow
    protected int frameCounter;
    @Shadow
    protected int tickCounter;

    @Unique
    private boolean rustedIronCore$interpolateFrames;
    @Unique
    private int[] rustedIronCore$interpolatedFrame;

    @Inject(method = "loadSprite", at = @At("RETURN"))
    private void retainInterpolateMetadata(Resource resource, CallbackInfo ci) {
        AnimationMetadataSection sourceMetadata = (AnimationMetadataSection) resource.getMetadata("animation");
        this.rustedIronCore$interpolateFrames = sourceMetadata != null
                && ((InterpolatedAnimationMetadata) sourceMetadata).rustedIronCore$interpolateFrames();
    }

    @Inject(method = "updateAnimation", at = @At("TAIL"))
    private void uploadInterpolatedFrame(CallbackInfo ci) {
        if (!this.rustedIronCore$interpolateFrames || this.animationMetadata == null || this.tickCounter <= 0) {
            return;
        }

        int frameTime = this.animationMetadata.getFrameTimeSingle(this.frameCounter);
        if (this.tickCounter >= frameTime) {
            return;
        }

        int frameCount = this.animationMetadata.getFrameCount();
        if (frameCount <= 1) {
            return;
        }

        int currentIndex = this.animationMetadata.getFrameIndex(this.frameCounter);
        int nextIndex = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % frameCount);
        if (currentIndex == nextIndex || currentIndex < 0 || nextIndex < 0
                || currentIndex >= this.framesTextureData.size() || nextIndex >= this.framesTextureData.size()) {
            return;
        }

        int[] currentFrame = this.framesTextureData.get(currentIndex);
        int[] nextFrame = this.framesTextureData.get(nextIndex);
        int pixelCount = this.width * this.height;
        if (currentFrame == null || nextFrame == null
                || currentFrame.length < pixelCount || nextFrame.length < pixelCount) {
            return;
        }
        if (this.rustedIronCore$interpolatedFrame == null
                || this.rustedIronCore$interpolatedFrame.length != pixelCount) {
            this.rustedIronCore$interpolatedFrame = new int[pixelCount];
        }

        double currentWeight = 1.0D - (double) this.tickCounter / (double) frameTime;
        for (int i = 0; i < pixelCount; i++) {
            int current = currentFrame[i];
            int next = nextFrame[i];
            int red = mix(currentWeight, current >> 16 & 255, next >> 16 & 255);
            int green = mix(currentWeight, current >> 8 & 255, next >> 8 & 255);
            int blue = mix(currentWeight, current & 255, next & 255);
            this.rustedIronCore$interpolatedFrame[i] = current & 0xFF000000 | red << 16 | green << 8 | blue;
        }

        TextureUtil.uploadTextureSub(this.rustedIronCore$interpolatedFrame, this.width, this.height,
                this.originX, this.originY, false, false);
    }

    @Unique
    private static int mix(double currentWeight, int current, int next) {
        return (int) (currentWeight * current + (1.0D - currentWeight) * next);
    }
}
