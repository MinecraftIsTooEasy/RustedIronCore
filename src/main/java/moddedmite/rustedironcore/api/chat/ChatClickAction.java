package moddedmite.rustedironcore.api.chat;

import javax.annotation.Nullable;

/**
 * Click actions supported by modern Minecraft chat components.
 */
public enum ChatClickAction {

    OPEN_URL("open_url", true),
    OPEN_FILE("open_file", false),
    RUN_COMMAND("run_command", true),
    SUGGEST_COMMAND("suggest_command", true),
    CHANGE_PAGE("change_page", true),
    COPY_TO_CLIPBOARD("copy_to_clipboard", true);

    private final String serializedName;
    private final boolean allowedFromServer;

    ChatClickAction(String serializedName, boolean allowedFromServer) {
        this.serializedName = serializedName;
        this.allowedFromServer = allowedFromServer;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public boolean isAllowedFromServer() {
        return this.allowedFromServer;
    }

    @Nullable
    public static ChatClickAction fromSerializedName(String name) {
        for (ChatClickAction action : values()) {
            if (action.serializedName.equals(name)) {
                return action;
            }
        }
        return null;
    }
}
