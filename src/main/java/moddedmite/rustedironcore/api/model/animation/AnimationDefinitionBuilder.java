package moddedmite.rustedironcore.api.model.animation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AnimationDefinitionBuilder {

    private final float lengthInSeconds;
    private final Map<String, List<AnimationChannel>> animationsByBone = new LinkedHashMap<>();
    private boolean looping;

    AnimationDefinitionBuilder(float lengthInSeconds) {
        if (!Float.isFinite(lengthInSeconds) || lengthInSeconds <= 0.0F) {
            throw new IllegalArgumentException("Animation length must be finite and positive");
        }
        this.lengthInSeconds = lengthInSeconds;
    }

    public AnimationDefinitionBuilder looping() {
        this.looping = true;
        return this;
    }

    public AnimationDefinitionBuilder addAnimation(String bone, AnimationChannel channel) {
        if (bone == null || bone.isBlank()) {
            throw new IllegalArgumentException("Animation bone name must not be blank");
        }
        Objects.requireNonNull(channel, "channel");
        this.animationsByBone.computeIfAbsent(bone, ignored -> new ArrayList<>())
                .add(channel);
        return this;
    }

    public AnimationDefinition build() {
        return new AnimationDefinition(this.lengthInSeconds, this.looping, this.animationsByBone);
    }
}
