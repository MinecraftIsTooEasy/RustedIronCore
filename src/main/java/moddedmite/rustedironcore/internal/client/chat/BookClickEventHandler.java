package moddedmite.rustedironcore.internal.client.chat;

import moddedmite.rustedironcore.api.chat.ChatClickEvent;
import moddedmite.rustedironcore.internal.chat.ChatClickSupport;
import net.minecraft.ChatAllowedCharacters;
import net.minecraft.GuiScreen;
import net.minecraft.Minecraft;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public final class BookClickEventHandler {

    public static boolean handle(BookClickEventContext context, ChatClickEvent event) {
        return switch (event.action()) {
            case CHANGE_PAGE -> changePage(context, event.value());
            case OPEN_URL -> openUrl(context, event.value());
            case OPEN_FILE -> openFile(context.ric$getMinecraft(), event.value());
            case RUN_COMMAND -> runCommand(context, event.value());
            case SUGGEST_COMMAND -> suggestCommand(context, event.value());
            case COPY_TO_CLIPBOARD -> copyToClipboard(event.value());
        };
    }

    public static void openUrl(URI uri) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (Exception ignored) {
        }
    }

    private static boolean changePage(BookClickEventContext context, String value) {
        try {
            int page = Integer.parseInt(value) - 1;
            if (page < 0 || page >= context.ric$getBookPageCount()) return false;
            context.ric$setBookPage(page);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static boolean openUrl(BookClickEventContext context, String value) {
        Minecraft minecraft = context.ric$getMinecraft();
        if (!minecraft.gameSettings.chatLinks) return false;

        URI uri;
        try {
            uri = ChatClickSupport.parseUntrustedHttpUrl(value);
        } catch (URISyntaxException exception) {
            return true;
        }
        if (minecraft.gameSettings.chatLinksPrompt) context.ric$confirmBookUrl(uri, value);
        else openUrl(uri);
        return true;
    }

    private static boolean openFile(Minecraft minecraft, String value) {
        if (!minecraft.isIntegratedServerRunning()) return true;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(new File(value));
            }
        } catch (Exception ignored) {
            return true;
        }
        return true;
    }

    private static boolean runCommand(BookClickEventContext context, String value) {
        Minecraft minecraft = context.ric$getMinecraft();
        String command = ChatAllowedCharacters.filerAllowedCharacters(value);
        if (command.startsWith("/") && minecraft.thePlayer != null) {
            minecraft.thePlayer.sendChatMessage(command);
            context.ric$closeBook();
        }
        return true;
    }

    private static boolean suggestCommand(BookClickEventContext context, String value) {
        context.ric$openBookChat(ChatAllowedCharacters.filerAllowedCharacters(value));
        return true;
    }

    private static boolean copyToClipboard(String value) {
        GuiScreen.setClipboardString(value);
        return true;
    }
}
