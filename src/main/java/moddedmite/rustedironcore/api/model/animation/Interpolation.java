package moddedmite.rustedironcore.api.model.animation;

import java.util.List;

@FunctionalInterface
public interface Interpolation {

    AnimationVector apply(float delta, List<Keyframe> keyframes, int currentIndex, int nextIndex, float scale);
}
