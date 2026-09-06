package moddedmite.rustedironcore.api.model.animation;

import net.minecraft.ModelRenderer;

import java.util.List;
import java.util.Map;

public final class KeyframeAnimations {

    public static void animate(
            Map<String, ModelRenderer> modelParts,
            AnimationDefinition definition,
            float elapsedSeconds) {
        animate(modelParts, definition, elapsedSeconds, 1.0F);
    }

    public static void animate(
            Map<String, ModelRenderer> modelParts,
            AnimationDefinition definition,
            float elapsedSeconds,
            float scale) {
        float time = definition.looping()
                ? elapsedSeconds % definition.lengthInSeconds()
                : elapsedSeconds;
        for (Map.Entry<String, List<AnimationChannel>> entry : definition.boneAnimations().entrySet()) {
            ModelRenderer modelPart = modelParts.get(entry.getKey());
            if (modelPart == null) {
                continue;
            }
            for (AnimationChannel channel : entry.getValue()) {
                applyChannel(modelPart, channel, time, scale);
            }
        }
    }

    private static void applyChannel(
            ModelRenderer modelPart,
            AnimationChannel channel,
            float elapsedSeconds,
            float scale) {
        List<Keyframe> keyframes = channel.keyframes();
        if (keyframes.isEmpty()) {
            return;
        }
        int nextIndex = findNextKeyframe(keyframes, elapsedSeconds);
        int currentIndex = Math.max(0, nextIndex - 1);
        nextIndex = Math.min(keyframes.size() - 1, currentIndex + 1);
        Keyframe current = keyframes.get(currentIndex);
        Keyframe next = keyframes.get(nextIndex);
        float duration = next.timestamp() - current.timestamp();
        float delta = nextIndex == currentIndex || duration <= 0.0F
                ? 0.0F
                : clamp((elapsedSeconds - current.timestamp()) / duration);
        AnimationVector value = next.interpolation().apply(delta, keyframes, currentIndex, nextIndex, scale);
        if (channel.target() == AnimationTarget.POSITION) {
            modelPart.rotationPointX += value.x();
            modelPart.rotationPointY += value.y();
            modelPart.rotationPointZ += value.z();
        } else if (channel.target() == AnimationTarget.ROTATION) {
            modelPart.rotateAngleX += value.x();
            modelPart.rotateAngleY += value.y();
            modelPart.rotateAngleZ += value.z();
        } else {
            throw new IllegalStateException("Unsupported animation target: " + channel.target());
        }
    }

    private static int findNextKeyframe(List<Keyframe> keyframes, float elapsedSeconds) {
        int low = 0;
        int high = keyframes.size();
        while (low < high) {
            int middle = low + (high - low) / 2;
            if (elapsedSeconds <= keyframes.get(middle).timestamp()) {
                high = middle;
            } else {
                low = middle + 1;
            }
        }
        return Math.min(keyframes.size() - 1, low);
    }

    private static float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    public static AnimationVector posVec(float x, float y, float z) {
        return new AnimationVector(x, -y, z);
    }

    public static AnimationVector degreeVec(float xDegrees, float yDegrees, float zDegrees) {
        float radians = (float) Math.PI / 180.0F;
        return new AnimationVector(xDegrees * radians, yDegrees * radians, zDegrees * radians);
    }
}
