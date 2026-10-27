package moddedmite.rustedironcore.internal.command;

import com.google.gson.JsonParseException;
import moddedmite.rustedironcore.api.chat.ChatComponents;
import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextComponentSerializer;
import moddedmite.rustedironcore.internal.event.listeners.CommandRegistry;
import net.minecraft.ChatMessageComponent;
import net.minecraft.CommandBase;
import net.minecraft.EnumChatFormatting;
import net.minecraft.ICommandSender;
import net.minecraft.PlayerSelector;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TellRawCommand extends CommandBase {

    private static final List<String> SELECTOR_SUGGESTIONS = List.of("@a", "@p", "@r", "@s");

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandName() {
        return "tellraw";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.tellraw.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] arguments) {
        if (arguments.length < 2) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Usage: /tellraw <targets> <component-json>")
                    .setColor(EnumChatFormatting.RED));
            return;
        }

        ServerPlayer[] targets = resolveTargets(sender, arguments[0]);
        if (targets.length == 0) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("No players matched target: " + arguments[0])
                    .setColor(EnumChatFormatting.RED));
            return;
        }

        TextComponent component;
        try {
            component = TextComponentSerializer.fromJson(joinArguments(arguments, 1));
        } catch (JsonParseException exception) {
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Invalid text component JSON: " + exception.getMessage())
                    .setColor(EnumChatFormatting.RED));
            return;
        }

        for (ServerPlayer target : targets) {
            TextComponent resolved = ChatComponents.resolveFor(component, sender, target);
            target.sendChatToPlayer(ChatComponents.create(resolved));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] arguments) {
        if (arguments.length != 1) return null;

        List<String> targets = new ArrayList<>(Arrays.asList(MinecraftServer.getServer().getAllUsernames()));
        targets.addAll(SELECTOR_SUGGESTIONS);
        return getListOfStringsFromIterableMatchingLastWord(arguments, targets);
    }

    private static ServerPlayer[] resolveTargets(ICommandSender sender, String target) {
        if ("@s".equals(target)) {
            return sender instanceof ServerPlayer player ? new ServerPlayer[]{player} : new ServerPlayer[0];
        }

        ServerPlayer[] selected = PlayerSelector.matchPlayers(sender, target);
        if (selected != null) return selected;

        ServerPlayer player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(target);
        return player == null ? new ServerPlayer[0] : new ServerPlayer[]{player};
    }

    private static String joinArguments(String[] arguments, int start) {
        StringBuilder json = new StringBuilder();
        for (int index = start; index < arguments.length; index++) {
            if (index > start) json.append(' ');
            json.append(arguments[index]);
        }
        return json.toString();
    }
}
