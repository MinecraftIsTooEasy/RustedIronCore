package moddedmite.rustedironcore.api.event.events;

import net.minecraft.ICommand;

import java.util.function.Consumer;

public class CommandRegisterEvent {
    final Consumer<ICommand> registry;

    public CommandRegisterEvent(Consumer<ICommand> registry) {
        this.registry = registry;
    }

    public void register(ICommand command) {
        this.registry.accept(command);
    }
}
