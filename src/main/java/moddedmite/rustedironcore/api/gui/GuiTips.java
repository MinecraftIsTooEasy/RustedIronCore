package moddedmite.rustedironcore.api.gui;

import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.I18n;
import net.minecraft.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTips extends GuiScreen {
    private static final ResourceLocation demoGuiTextures = new ResourceLocation("textures/gui/demo_background.png");
    private int counter = 1800;

    public GuiTips() {
    }

    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height / 2 + 90, 114, 20, I18n.getString("gui.done") + " (" + counter / 20 + ")"));
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 1 && counter == 0) {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
    }

    public void drawDefaultBackground() {
        super.drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(demoGuiTextures);
        int var1 = (this.width - 248) / 2;
        int var2 = (this.height - 166) / 2;
        this.drawTexturedModalRect(var1, var2, 0, 0, 248, 166);
    }

    public void drawScreen(int par1, int par2, float par3) {
        int x = (this.width - 248) / 2 + 10;
        int y = (this.height - 166) / 2 + 8;
        this.fontRenderer.drawString(I18n.getString("ric.statement"), x, y, 2039583);
        counter--;
    }
}