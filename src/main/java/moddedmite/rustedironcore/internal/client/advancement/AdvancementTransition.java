package moddedmite.rustedironcore.internal.client.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AdvancementTransition {

    private static final long DURATION_NANOS = 150_000_000L;
    private double value, start, target;
    private long startedAt;

    public AdvancementTransition(double value) {
        reset(value);
    }

    public double value() {
        return value;
    }

    public double target() {
        return target;
    }

    public void animateTo(double target) {
        if (this.target == target) return;
        long now = System.nanoTime();
        start = update(now);
        this.target = target;
        startedAt = now;
    }

    public double update(long now) {
        double progress = Math.max(0, Math.min(1, (now - startedAt) / (double) DURATION_NANOS));
        double remaining = 1 - progress;
        value = progress == 1 ? target : start + (target - start) * (1 - remaining * remaining * remaining);
        return value;
    }

    public void reset(double value) {
        this.value = start = target = value;
    }
}
