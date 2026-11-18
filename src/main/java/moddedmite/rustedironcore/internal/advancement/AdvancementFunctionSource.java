package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.ChatMessageComponent;
import net.minecraft.ChunkCoordinates;
import net.minecraft.ICommandSender;
import net.minecraft.ServerPlayer;
import net.minecraft.World;

/** Function rewards use the recipient's position and the modern function permission level. */
public record AdvancementFunctionSource(ServerPlayer player) implements ICommandSender {

    public String resolveSelf(String command) {
        StringBuilder result = new StringBuilder();
        char quote = 0;
        boolean escaped = false;
        for (int i = 0; i < command.length(); i++) {
            char current = command.charAt(i);
            if (escaped) { result.append(current); escaped = false; continue; }
            if (current == '\\' && quote != 0) { result.append(current); escaped = true; continue; }
            if (current == '"' || current == '\'') {
                if (quote == current) quote = 0;
                else if (quote == 0) quote = current;
            }
            if (quote == 0 && current == '@' && command.startsWith("@s", i)
                    && (i == 0 || Character.isWhitespace(command.charAt(i - 1)))
                    && (i + 2 == command.length() || Character.isWhitespace(command.charAt(i + 2)))) {
                result.append(player.getCommandSenderName());
                i++;
            } else result.append(current);
        }
        return result.toString();
    }

    @Override
    public String getCommandSenderName() {
        return player.getCommandSenderName();
    }

    @Override
    public void sendChatToPlayer(ChatMessageComponent message) {
    }

    @Override
    public boolean canCommandSenderUseCommand(int level, String command) {
        return level <= 2;
    }

    @Override
    public ChunkCoordinates getPlayerCoordinates() {
        return player.getPlayerCoordinates();
    }

    @Override
    public World getEntityWorld() {
        return player.worldObj;
    }
}
