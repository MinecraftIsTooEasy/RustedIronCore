package moddedmite.rustedironcore.api.event;

/**
 * Only called at specific stages of game init.
 */
public class StagedHandler extends AbstractHandler<Runnable> {
    public void publish() {
        this.listeners.forEach(Runnable::run);
    }
}
