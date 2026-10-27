package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.GuiTextField;
import net.minecraft.Minecraft;

import java.net.URI;

public interface ChatClickEventContext {

    Minecraft ric$getMinecraft();

    GuiTextField ric$getInputField();

    void ric$confirmOpenUrl(URI uri, String value);

    void ric$openUrl(URI uri);

    void ric$refreshCommandSuggestions();
}
