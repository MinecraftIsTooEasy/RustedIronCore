package moddedmite.rustedironcore.internal.client.chat;

import net.minecraft.Minecraft;

import java.net.URI;

public interface BookClickEventContext {

    Minecraft ric$getMinecraft();

    int ric$getBookPageCount();

    void ric$setBookPage(int page);

    void ric$confirmBookUrl(URI uri, String value);

    void ric$openBookChat(String value);

    void ric$closeBook();
}
