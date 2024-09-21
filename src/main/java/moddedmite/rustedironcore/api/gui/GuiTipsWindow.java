package moddedmite.rustedironcore.api.gui;

import net.minecraft.*;
import net.minecraft.client.main.Main;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiTipsWindow extends Gui {
    private static final ResourceLocation achievementTextures = new ResourceLocation("textures/gui/achievement_background.png");
    private static Minecraft theGame;
    private static RenderItem itemRender;
    private static int tipsWindowWidth;
    private static int tipsWindowHeight;
    public static boolean haveItem;
    public static ItemStack itemStack;
    private static String tipTitle;
    private static String tip;

    public GuiTipsWindow(Minecraft par1Minecraft) {
        this.theGame = par1Minecraft;
        this.itemRender = new RenderItem();
    }

    public void informationNoTitle(String tip) {
        this.tip = tip;
    }

    public void information(String tipTitle, String tip) {
        this.tipTitle = tipTitle;
        this.tip = tip;
    }

    public void informationHaveItemStack(String tipTitle, String tip, ItemStack itemStack) {
        this.tipTitle = tipTitle;
        this.tip = tip;
        this.haveItem = true;
        this.itemStack = itemStack;
    }


    private static void updateAchievementWindowScale() {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        tipsWindowWidth = mc.displayWidth;
        tipsWindowHeight = mc.displayHeight;
        ScaledResolution var1 = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        tipsWindowWidth = var1.getScaledWidth();
        tipsWindowHeight = var1.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) tipsWindowWidth, (double) tipsWindowHeight, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public static void updateAchievementWindow(Gui gui, String tipTitle, String tip) {
        Minecraft mc = Minecraft.getMinecraft();
        if (Minecraft.theMinecraft.gameSettings.gui_mode == 0 && !Main.is_MITE_DS) {
            updateAchievementWindowScale();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            double var1 = (double)(Minecraft.getSystemTime() - Minecraft.getSystemTime() - 2500L) / 3000.0D;
            double var3 = var1 * 2.0D;

            if (var3 > 1.0D) {
                var3 = 2.0D - var3;
            }

            var3 *= 4.0D;
            var3 = 1.0D - var3;

            if (var3 < 0.0D) {
                var3 = 0.0D;
            }

            var3 *= var3;
            var3 *= var3;
            int var5 = tipsWindowWidth - 160;
            int var6 = 0 - (int) (var3 * 36.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            mc.getTextureManager().bindTexture(achievementTextures);
            GL11.glDisable(GL11.GL_LIGHTING);
            gui.drawTexturedModalRect(var5, var6, 96, 202, 160, 32);

            mc.fontRenderer.drawString(tipTitle, var5 + 30, var6 + 7, -256);
            mc.fontRenderer.drawString(tip, var5 + 30, var6 + 18, -1);

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            if (haveItem) {
                itemRender.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, var5 + 8, var6 + 8);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }

}
