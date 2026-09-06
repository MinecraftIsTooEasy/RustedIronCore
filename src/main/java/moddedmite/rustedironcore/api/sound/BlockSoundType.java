package moddedmite.rustedironcore.api.sound;

import net.minecraft.StepSound;

import javax.annotation.Nonnull;

/**
 * Complete modern block-sound contract for MITE.
 *
 * <p>Implement this interface on a {@link StepSound} subclass and assign that
 * instance to a block with {@code setStepSound}. RIC then uses the five event
 * getters at the matching MITE sound call sites. Implementations may calculate
 * values dynamically; mods that only need an immutable value object can use
 * {@link BlockSoundSet} directly.</p>
 */
public interface BlockSoundType {

    /**
     * Returns the volume shared by all block sound events.
     *
     * @return the shared volume
     */
    float getVolume();

    /**
     * Returns the pitch shared by all block sound events.
     *
     * @return the shared pitch
     */
    float getPitch();

    /**
     * Returns the sound played when the block is broken.
     *
     * @return the namespaced block-break sound identifier
     */
    @Nonnull
    String getBreakSound();

    /**
     * Returns the sound played when an entity walks on the block.
     *
     * @return the namespaced block-step sound identifier
     */
    @Nonnull
    String getStepSound();

    /**
     * Returns the sound played when the block is placed.
     *
     * @return the namespaced block-place sound identifier
     */
    @Nonnull
    String getPlaceSound();

    /**
     * Returns the sound played periodically while the block is being mined.
     *
     * @return the namespaced block-hit sound identifier
     */
    @Nonnull
    String getHitSound();

    /**
     * Returns the sound played when a living entity takes fall damage on the block.
     *
     * @return the namespaced block-fall sound identifier
     */
    @Nonnull
    String getFallSound();
}
