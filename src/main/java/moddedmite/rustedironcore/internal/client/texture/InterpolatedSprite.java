package moddedmite.rustedironcore.internal.client.texture;

import net.minecraft.AnimationMetadataSection;

/** A {@code TextureAtlasSprite} that blends animation frames when its metadata asks for {@code interpolate}. */
public interface InterpolatedSprite {
    /** Called after the sprite was loaded with {@code sourceMetadata} (the "animation" section of its resource, or null). */
    void rustedIronCore$retainInterpolation(AnimationMetadataSection sourceMetadata);
}
