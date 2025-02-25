package moddedmite.rustedironcore.internal.command;

import moddedmite.rustedironcore.api.world.Dimension;
import moddedmite.rustedironcore.internal.event.listeners.CommandRegistry;
import net.minecraft.CommandBase;
import net.minecraft.ICommandSender;

import java.util.List;

public class DimensionCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandName() {
        return CommandRegistry.COMMAND_PREFIX + "dimension";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "commands." + this.getCommandName() + "usage";
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        int length = strings.length;
        if (length == 1) {
            String string = strings[0];
            Dimension dimension;
            dimension = Dimension.fromString(string);
            if (dimension == null) dimension = Dimension.fromId(parseInt(iCommandSender, string));
            if (dimension == null) return;
            getCommandSenderAsPlayer(iCommandSender).travelToDimension(dimension.id());
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
        int length = par2ArrayOfStr.length;
        if (length == 1) return Dimension.streamDimensions().map(Dimension::name).toList();
        return null;
    }
}
