package moddedmite.rustedironcore.api.event.events;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ICommandSender;
import net.minecraft.ICommand;

import java.util.function.Consumer;

public class CommandRegisterEvent {
    final Consumer<ICommand> registry;
    private final Consumer<LiteralArgumentBuilder<ICommandSender>> suggestionRegistry;

    public CommandRegisterEvent(Consumer<ICommand> registry,
                                Consumer<LiteralArgumentBuilder<ICommandSender>> suggestionRegistry) {
        this.registry = registry;
        this.suggestionRegistry = suggestionRegistry;
    }

    public void register(ICommand command) {
        this.registry.accept(command);
    }

    /** Registers Brigadier syntax used for parsing, suggestions, and usage hints for an ICommand. */
    public void registerSuggestionTree(LiteralArgumentBuilder<ICommandSender> command) {
        this.suggestionRegistry.accept(command);
    }
}
