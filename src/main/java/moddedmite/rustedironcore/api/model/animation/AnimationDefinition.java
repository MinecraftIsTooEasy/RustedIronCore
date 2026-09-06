package moddedmite.rustedironcore.api.model.animation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record AnimationDefinition(float lengthInSeconds, boolean looping, Map<String, List<AnimationChannel>> boneAnimations) {

    public AnimationDefinition {
        if (!Float.isFinite(lengthInSeconds) || lengthInSeconds <= 0.0F) {
            throw new IllegalArgumentException("Animation length must be finite and positive");
        }
        Objects.requireNonNull(boneAnimations, "boneAnimations");
        Map<String, List<AnimationChannel>> animations = new LinkedHashMap<>();
        boneAnimations.forEach((bone, channels) -> {
            if (bone == null || bone.isBlank()) {
                throw new IllegalArgumentException("Animation bone name must not be blank");
            }
            animations.put(bone, List.copyOf(channels));
        });
        boneAnimations = Collections.unmodifiableMap(animations);
    }

    public static AnimationDefinitionBuilder builder(float lengthInSeconds) {
        return new AnimationDefinitionBuilder(lengthInSeconds);
    }
}
