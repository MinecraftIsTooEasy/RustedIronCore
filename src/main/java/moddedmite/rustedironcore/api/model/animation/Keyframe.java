package moddedmite.rustedironcore.api.model.animation;

import java.util.Objects;

public record Keyframe(float timestamp, AnimationVector target, Interpolation interpolation) {

    public Keyframe {
        if (!Float.isFinite(timestamp) || timestamp < 0.0F) {
            throw new IllegalArgumentException("Keyframe timestamp must be finite and non-negative");
        }
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(interpolation, "interpolation");
    }
}
