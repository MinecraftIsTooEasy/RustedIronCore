package moddedmite.rustedironcore.mixin.client.gui;

import moddedmite.rustedironcore.api.chat.TextComponent;
import moddedmite.rustedironcore.api.chat.TextStyle;
import moddedmite.rustedironcore.internal.client.chat.BookClickEventContext;
import moddedmite.rustedironcore.internal.client.chat.BookClickEventHandler;
import moddedmite.rustedironcore.internal.client.chat.BookPageText;
import moddedmite.rustedironcore.internal.client.chat.BookScreenAccess;
import moddedmite.rustedironcore.internal.client.chat.ChatInteractionRenderer;
import moddedmite.rustedironcore.internal.client.chat.MiteTextComponentAdapter;
import net.minecraft.FontRenderer;
import net.minecraft.GuiChat;
import net.minecraft.GuiConfirmOpenLink;
import net.minecraft.GuiScreen;
import net.minecraft.GuiScreenBook;
import net.minecraft.Minecraft;
import net.minecraft.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(GuiScreenBook.class)
public abstract class GuiScreenBookMixin extends GuiScreen implements BookScreenAccess, BookClickEventContext {

    @Shadow @Final private boolean bookIsUnsigned;
    @Shadow private boolean editingTitle;
    @Shadow private int currPage;
    @Shadow private int bookTotalPages;
    @Shadow private int bookImageWidth;
    @Shadow private NBTTagList bookPages;

    @Unique private TextComponent ric$visiblePage;
    @Unique private URI ric$pendingUrl;

    @Redirect(method = "drawScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/FontRenderer;drawSplitString(Ljava/lang/String;IIII)V"))
    private void renderStructuredPage(FontRenderer renderer, String text, int x, int y, int width, int color) {
        TextComponent component = BookPageText.readPage(this.bookPages, this.currPage);
        if (component == null || this.editingTitle || this.bookIsUnsigned) {
            this.ric$visiblePage = null;
            renderer.drawSplitString(text, x, y, width, color);
            return;
        }
        try {
            this.ric$visiblePage = component;
            renderer.drawSplitString(MiteTextComponentAdapter.toFormattedString(component), x, y, width, color);
        } catch (UnsupportedOperationException exception) {
            this.ric$visiblePage = null;
            renderer.drawSplitString(text, x, y, width, color);
        }
    }

    @Inject(method = "drawScreen", at = @At("RETURN"))
    private void renderBookComponentHover(int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        TextStyle style = BookPageText.styleAt(this.ric$visiblePage, this.fontRenderer, this.width,
                this.bookImageWidth, mouseX, mouseY);
        if (style != null) {
            ChatInteractionRenderer.renderHoverTooltip(this.fontRenderer, style.getHoverEvent(), mouseX, mouseY,
                    this.width, this.height);
        }
    }

    @Override
    public boolean ric$handleBookComponentClick(int mouseX, int mouseY, int button) {
        if (button != 0 || this.editingTitle || this.bookIsUnsigned) return false;
        TextStyle style = BookPageText.styleAt(this.ric$visiblePage, this.fontRenderer, this.width,
                this.bookImageWidth, mouseX, mouseY);
        return style != null && style.getClickEvent() != null
                && BookClickEventHandler.handle(this, style.getClickEvent());
    }

    @Override
    public Minecraft ric$getMinecraft() {
        return this.mc;
    }

    @Override
    public int ric$getBookPageCount() {
        return this.bookTotalPages;
    }

    @Override
    public void ric$setBookPage(int page) {
        this.currPage = page;
    }

    @Override
    public void ric$confirmBookUrl(URI uri, String value) {
        this.ric$pendingUrl = uri;
        this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, value, 0, false));
    }

    @Override
    public void ric$openBookChat(String value) {
        this.mc.displayGuiScreen(new GuiChat(value));
    }

    @Override
    public void ric$closeBook() {
        this.mc.displayGuiScreen(null);
    }

    @Override
    public void confirmClicked(boolean accepted, int id) {
        URI uri = this.ric$pendingUrl;
        this.ric$pendingUrl = null;
        this.mc.displayGuiScreen(this);
        if (accepted && uri != null) BookClickEventHandler.openUrl(uri);
    }

}
