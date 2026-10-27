package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.internal.chat.ChatClickSupport;
import moddedmite.rustedironcore.internal.client.ClientBootstrap;
import net.minecraft.ChatAllowedCharacters;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class ChatClickEventHandler {

    public static boolean handle(ChatClickEventContext context, ChatClickEvent event) {
        return switch (event.action()) {
            case OPEN_URL -> openUrl(context, event.value());
            case OPEN_FILE -> openFile(event.value());
            case SUGGEST_COMMAND -> suggestCommand(context, event.value());
            case RUN_COMMAND -> runCommand(context.ric$getMinecraft(), event.value());
            case COPY_TO_CLIPBOARD -> copyToClipboard(event.value());
            case CHANGE_PAGE -> {
                ClientBootstrap.LOGGER.error("Don't know how to handle chat click event: {}", event);
                yield true;
            }
        };
    }

    private static boolean openUrl(ChatClickEventContext context, String value) {
        Minecraft minecraft = context.ric$getMinecraft();
        if (!minecraft.gameSettings.chatLinks) return false;

        URI uri;
        try {
            uri = ChatClickSupport.parseUntrustedHttpUrl(value);
        } catch (URISyntaxException exception) {
            ClientBootstrap.LOGGER.error("Can't open URL from chat click event: {}", value);
            return true;
        }
        if (minecraft.gameSettings.chatLinksPrompt) context.ric$confirmOpenUrl(uri, value);
        else context.ric$openUrl(uri);
        return true;
    }

    private static boolean openFile(String path) {
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                ClientBootstrap.LOGGER.error("The current platform does not support opening files");
                return true;
            }
            Desktop.getDesktop().open(new File(path));
        } catch (Exception exception) {
            ClientBootstrap.LOGGER.error("Can't open file from chat click event: {}", path, exception);
        }
        return true;
    }

    private static boolean suggestCommand(ChatClickEventContext context, String command) {
        context.ric$getInputField().setText(ChatAllowedCharacters.filerAllowedCharacters(command));
        context.ric$refreshCommandSuggestions();
        return true;
    }

    private static boolean runCommand(Minecraft minecraft, String command) {
        String filtered = ChatAllowedCharacters.filerAllowedCharacters(command);
        if (!filtered.startsWith("/")) {
            ClientBootstrap.LOGGER.error("Failed to run command without '/' prefix from click event: '{}'", filtered);
            return true;
        }
        minecraft.thePlayer.sendChatMessage(filtered);
        return true;
    }

    private static boolean copyToClipboard(String value) {
        GuiScreen.setClipboardString(value);
        return true;
    }
}
