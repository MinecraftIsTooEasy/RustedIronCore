package moddedmite.rustedironcore.api.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class TimedTaskHandler {
    private final List<TimedTask> timedTasks = new ArrayList<>();

    public void registerTimedTask(int ticks, BooleanSupplier condition, Runnable task) {
        if (ticks <= 0) throw new IllegalArgumentException();
        this.timedTasks.add(new TimedTask(ticks, condition, task));
    }

    public void onTick() {
        if (this.timedTasks.isEmpty()) return;
        this.timedTasks.removeIf(TimedTask::onTick);
    }

    private static class TimedTask {
        int ticks;
        BooleanSupplier condition;
        final Runnable task;

        public TimedTask(int ticks, BooleanSupplier condition, Runnable task) {
            this.ticks = ticks;
            this.condition = condition;
            this.task = task;
        }

        private boolean onTick() {// true means should remove
            if (this.ticks == 0) {
                if (condition.getAsBoolean()) {
                    this.task.run();
                    return true;
                } else {
                    return false;
                }
            }
            this.ticks -= 1;
            return false;
        }
    }
}
