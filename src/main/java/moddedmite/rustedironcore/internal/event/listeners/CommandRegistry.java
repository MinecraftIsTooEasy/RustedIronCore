package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.CommandRegisterEvent;
import moddedmite.rustedironcore.internal.command.DimensionCommand;

import java.util.function.Consumer;

public class CommandRegistry implements Consumer<CommandRegisterEvent> {
    public static final String COMMAND_PREFIX = "ric";

    @Override
    public void accept(CommandRegisterEvent event) {
        event.register(new DimensionCommand());
    }
}
