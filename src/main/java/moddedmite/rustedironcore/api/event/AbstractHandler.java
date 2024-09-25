package moddedmite.rustedironcore.api.event;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHandler<T> {
    protected final List<T> listeners = new ArrayList<>();

    public void register(T t) {
        if (!this.listeners.contains(t)) {
            this.listeners.add(t);
        }
    }

    public void unregister(T t) {
        this.listeners.remove(t);
    }

    protected List<T> getListeners() {
        return this.listeners;
    }
}
