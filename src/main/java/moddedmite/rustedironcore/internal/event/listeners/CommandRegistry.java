package moddedmite.rustedironcore.internal.event.listeners;

import moddedmite.rustedironcore.api.event.events.CommandRegisterEvent;
import moddedmite.rustedironcore.internal.command.TellRawCommand;
import moddedmite.rustedironcore.internal.command.DimensionCommand;
import moddedmite.rustedironcore.internal.command.CommandSuggestionDispatcher;
import moddedmite.rustedironcore.internal.command.MiteCommandTrees;
import moddedmite.rustedironcore.internal.config.RICConfig;

import java.util.function.Consumer;

public class CommandRegistry implements Consumer<CommandRegisterEvent> {
    public static final String COMMAND_PREFIX = "ric";

    @Override
    public void accept(CommandRegisterEvent event) {
        event.register(new TellRawCommand());
        MiteCommandTrees.register(CommandSuggestionDispatcher::registerBuiltin,
                CommandSuggestionDispatcher::registerStandalone);
        if (RICConfig.UseCustomDimension.get()) {
            event.register(new DimensionCommand());
        }
    }
}
