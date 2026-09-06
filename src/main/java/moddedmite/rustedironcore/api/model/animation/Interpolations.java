package moddedmite.rustedironcore.api.model.animation;

import java.util.List;

public enum Interpolations implements Interpolation {
    LINEAR(Interpolations::interpolateLinear),
    CATMULL_ROM(Interpolations::interpolateCatmullRom);

    private final Interpolation interpolation;

    Interpolations(Interpolation interpolation) {
        this.interpolation = interpolation;
    }

    @Override
    public AnimationVector apply(float delta, List<Keyframe> keyframes, int currentIndex, int nextIndex, float scale) {
        return this.interpolation.apply(delta, keyframes, currentIndex, nextIndex, scale);
    }

    private static AnimationVector interpolateLinear(float delta, List<Keyframe> keyframes, int currentIndex, int nextIndex, float scale) {
        AnimationVector current = keyframes.get(currentIndex).target();
        AnimationVector next = keyframes.get(nextIndex).target();
        return new AnimationVector(
                lerp(delta, current.x(), next.x()) * scale,
                lerp(delta, current.y(), next.y()) * scale,
                lerp(delta, current.z(), next.z()) * scale);
    }

    private static AnimationVector interpolateCatmullRom(float delta, List<Keyframe> keyframes, int currentIndex, int nextIndex, float scale) {
        AnimationVector previous = keyframes.get(Math.max(0, currentIndex - 1)).target();
        AnimationVector current = keyframes.get(currentIndex).target();
        AnimationVector next = keyframes.get(nextIndex).target();
        AnimationVector following = keyframes.get(Math.min(keyframes.size() - 1, nextIndex + 1)).target();
        return new AnimationVector(
                catmullRom(delta, previous.x(), current.x(), next.x(), following.x()) * scale,
                catmullRom(delta, previous.y(), current.y(), next.y(), following.y()) * scale,
                catmullRom(delta, previous.z(), current.z(), next.z(), following.z()) * scale);
    }

    private static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    private static float catmullRom(float delta, float previous, float current, float next, float following) {
        float squared = delta * delta;
        float cubed = squared * delta;
        return 0.5F * (2.0F * current
                + (next - previous) * delta
                + (2.0F * previous - 5.0F * current + 4.0F * next - following) * squared
                + (3.0F * current - previous - 3.0F * next + following) * cubed);
    }
}
