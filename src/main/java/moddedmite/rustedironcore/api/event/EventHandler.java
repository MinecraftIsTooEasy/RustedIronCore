package moddedmite.rustedironcore.api.event;

import java.util.function.Consumer;

public class EventHandler<T> extends AbstractHandler<Consumer<T>> {
    public void post(T t) {
        this.getListeners().forEach(x -> x.accept(t));
    }
}
