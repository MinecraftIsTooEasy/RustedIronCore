package moddedmite.rustedironcore.api.model.animation;

import java.util.List;
import java.util.Objects;

public record AnimationChannel(AnimationTarget target, List<Keyframe> keyframes) {

    public AnimationChannel {
        Objects.requireNonNull(target, "target");
        keyframes = List.copyOf(keyframes);
        for (int index = 1; index < keyframes.size(); index++) {
            if (keyframes.get(index - 1).timestamp() > keyframes.get(index).timestamp()) {
                throw new IllegalArgumentException("Keyframes must be ordered by timestamp");
            }
        }
    }

    public AnimationChannel(AnimationTarget target, Keyframe... keyframes) {
        this(target, List.of(keyframes));
    }
}
