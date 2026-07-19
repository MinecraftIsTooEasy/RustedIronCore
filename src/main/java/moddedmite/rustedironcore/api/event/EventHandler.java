package moddedmite.rustedironcore.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventHandler<T> extends AbstractHandler<Consumer<T>> {
    private final List<Consumer<T>> pre = new ArrayList<>();
    private final List<Consumer<T>> post = new ArrayList<>();

    public void registerPre(Consumer<T> t) {
        if (!this.pre.contains(t)) {
            this.pre.add(t);
        }
    }

    public void unregisterPre(Consumer<T> t) {
        this.pre.remove(t);
    }

    public void registerPost(Consumer<T> t) {
        if (!this.post.contains(t)) {
            this.post.add(t);
        }
    }

    public void unregisterPost(Consumer<T> t) {
        this.post.remove(t);
    }

    public void publish(T t) {
        this.pre.forEach(x -> x.accept(t));
        this.listeners.forEach(x -> x.accept(t));
        this.post.forEach(x -> x.accept(t));
    }
}
