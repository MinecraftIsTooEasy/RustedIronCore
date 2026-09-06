package moddedmite.rustedironcore.api.sound;

import net.minecraft.ResourceLocation;
import net.minecraft.StepSound;
import net.xiaoyu233.fml.reload.event.SoundsRegisterEvent;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Immutable standard implementation of {@link BlockSoundType} backed by MITE's
 * {@link StepSound}.
 *
 * <p>This class defines sound identifiers only. Register their audio resources
 * through {@link SoundsRegisterEvent} before assigning the sound set to a block.
 * All five sound identifiers are required.</p>
 */
public final class BlockSoundSet extends StepSound implements BlockSoundType {

    private final String breakSound;
    private final String stepSound;
    private final String placeSound;
    private final String hitSound;
    private final String fallSound;

    /**
     * Creates a complete modern block sound set.
     *
     * <p>The parameter order matches modern Minecraft's {@code SoundType}.</p>
     *
     * @param volume the volume shared by all sound events
     * @param pitch the pitch shared by all sound events
     * @param breakSound the sound identifier used when the block is broken
     * @param stepSound the sound identifier used for footsteps
     * @param placeSound the sound identifier used when the block is placed
     * @param hitSound the sound identifier used while the block is being mined
     * @param fallSound the sound identifier used when a living entity takes fall damage on the block
     */
    public BlockSoundSet(float volume,
                         float pitch,
                         @Nonnull ResourceLocation breakSound,
                         @Nonnull ResourceLocation stepSound,
                         @Nonnull ResourceLocation placeSound,
                         @Nonnull ResourceLocation hitSound,
                         @Nonnull ResourceLocation fallSound) {
        super(Objects.requireNonNull(stepSound, "stepSound").toString(), volume, pitch);
        this.breakSound = Objects.requireNonNull(breakSound, "breakSound").toString();
        this.stepSound = stepSound.toString();
        this.placeSound = Objects.requireNonNull(placeSound, "placeSound").toString();
        this.hitSound = Objects.requireNonNull(hitSound, "hitSound").toString();
        this.fallSound = Objects.requireNonNull(fallSound, "fallSound").toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return the namespaced sound identifier used when the block is broken
     */
    @Nonnull
    @Override
    public String getBreakSound() {
        return this.breakSound;
    }

    /**
     * {@inheritDoc}
     *
     * @return the namespaced sound identifier used for footsteps
     */
    @Nonnull
    @Override
    public String getStepSound() {
        return this.stepSound;
    }

    /**
     * {@inheritDoc}
     *
     * @return the namespaced sound identifier used when the block is placed
     */
    @Nonnull
    @Override
    public String getPlaceSound() {
        return this.placeSound;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getHitSound() {
        return this.hitSound;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getFallSound() {
        return this.fallSound;
    }
}
