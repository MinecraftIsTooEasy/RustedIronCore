package moddedmite.rustedironcore.mixin.client.gui;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import moddedmite.rustedironcore.internal.client.advancement.AchievementPages;
import moddedmite.rustedironcore.internal.client.advancement.AdvancementIcons;
import moddedmite.rustedironcore.internal.client.advancement.AdvancementTransition;
import moddedmite.rustedironcore.internal.client.advancement.ClientAdvancements;
import net.minecraft.Achievement;
import net.minecraft.AchievementList;
import net.minecraft.GuiAchievements;
import net.minecraft.GuiButton;
import net.minecraft.GuiScreen;
import net.minecraft.ItemStack;
import net.minecraft.FontRenderer;
import net.minecraft.RenderItem;
import net.minecraft.ScaledResolution;
import net.minecraft.TextureManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(GuiAchievements.class)
public abstract class GuiAchievementsMixin extends GuiScreen {

    @Shadow
    protected int achievementsPaneWidth;

    @Shadow
    protected int achievementsPaneHeight;

    @Shadow
    GuiButton button_cycle_view_mode;

    @Shadow
    protected double guiMapX;

    @Shadow
    protected double guiMapY;

    @Shadow
    protected double field_74117_m;

    @Shadow
    protected double field_74115_n;

    @Shadow
    protected double field_74124_q;

    @Shadow
    protected double field_74123_r;

    @Shadow
    @Final
    private static int guiMapTop;

    @Shadow
    @Final
    private static int guiMapLeft;

    @Shadow
    @Final
    private static int guiMapBottom;

    @Shadow
    @Final
    private static int guiMapRight;

    @Unique
    private AchievementPages ric$pages;

    @Unique
    private double ric$nativeZoom = 1;

    @Unique
    private final AdvancementTransition ric$nativeZoomTransition = new AdvancementTransition(1);

    @Unique
    private double ric$zoomAnchorX, ric$zoomAnchorY;

    @Unique
    private int ric$dragX, ric$dragY, ric$renderMouseX, ric$renderMouseY;

    @Unique
    private boolean ric$nativeDragging, ric$nativeTransform;

    @Unique
    private int ric$verticalOffset() {
        return Math.min(14, Math.max(0, (height - achievementsPaneHeight) / 2 - 4));
    }

    @Unique
    private boolean ric$resizeWindow() {
        int oldWidth = achievementsPaneWidth, oldHeight = achievementsPaneHeight;
        int oldLeft = (width - oldWidth) / 2;
        int oldTop = (height - oldHeight) / 2 + ric$verticalOffset();
        int maxWidth = Math.max(1, width - 48), maxHeight = Math.max(1, height - 38);
        int minWidth = Math.min(256, maxWidth), minHeight = Math.min(202, maxHeight);
        achievementsPaneWidth = minWidth + (int) Math.round((maxWidth - minWidth) * ric$pages.windowSize());
        achievementsPaneHeight = minHeight + (int) Math.round((maxHeight - minHeight) * ric$pages.windowSize());
        if (oldWidth == achievementsPaneWidth && oldHeight == achievementsPaneHeight) return false;
        int left = (width - achievementsPaneWidth) / 2;
        int top = (height - achievementsPaneHeight) / 2 + ric$verticalOffset();
        ric$setNativeScroll(guiMapX + (left - oldLeft) / ric$nativeZoom,
                guiMapY + (top - oldTop) / ric$nativeZoom);
        return true;
    }

    @Unique
    private void ric$updateNativeZoom(long now) {
        double nextZoom = ric$nativeZoomTransition.update(now);
        if (nextZoom == ric$nativeZoom) return;
        double x = ric$zoomAnchorX * ric$pages.contentWidth(), y = ric$zoomAnchorY * ric$pages.contentHeight();
        double scrollX = guiMapX + x * (1 / ric$nativeZoom - 1 / nextZoom);
        double scrollY = guiMapY + y * (1 / ric$nativeZoom - 1 / nextZoom);
        ric$nativeZoom = nextZoom;
        ric$setNativeScroll(scrollX, scrollY);
    }

    @Unique
    private void ric$positionButtons() {
        int center = (width - achievementsPaneWidth) / 2 + achievementsPaneWidth / 2;
        int y = (height - achievementsPaneHeight) / 2 + ric$verticalOffset() + achievementsPaneHeight - 28;
        for (Object entry : buttonList) {
            GuiButton button = (GuiButton) entry;
            if (button.id == 1) {
                button.xPosition = center + (button_cycle_view_mode.drawButton ? 24 : -40);
                button.yPosition = y;
            } else if (button == button_cycle_view_mode) {
                button.xPosition = center - 104;
                button.yPosition = y;
            }
        }
    }

    @Unique
    private void ric$setNativeScroll(double x, double y) {
        int xOffset = ric$nativeXOffset(), yOffset = ric$nativeYOffset();
        guiMapX = field_74117_m = field_74124_q = Math.max(guiMapTop + xOffset, Math.min(guiMapBottom + xOffset - 1, x));
        guiMapY = field_74115_n = field_74123_r = Math.max(guiMapLeft + yOffset, Math.min(guiMapRight + yOffset - 1, y));
    }

    @Unique
    private int ric$nativeXOffset() {
        return (int) Math.floor((224 - (achievementsPaneWidth - 32) / ric$nativeZoom) / 2);
    }

    @Unique
    private int ric$nativeYOffset() {
        return (int) Math.floor((155 - (achievementsPaneHeight - 47) / ric$nativeZoom) / 2);
    }

    @Unique
    private void ric$dragNativePage(int x, int y) {
        if (!ric$nativeDragging) return;
        ric$setNativeScroll(guiMapX - (x - ric$dragX) / ric$nativeZoom, guiMapY - (y - ric$dragY) / ric$nativeZoom);
        ric$dragX = x;
        ric$dragY = y;
    }

    @Unique
    private void ric$endNativeTransform() {
        if (!ric$nativeTransform) return;
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        ric$nativeTransform = false;
    }

    @ModifyExpressionValue(method = {"initGui()V", "drawScreen(IIF)V", "genAchievementBackground(IIF)V", "drawTitle()V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/GuiAchievements;height:I", opcode = Opcodes.GETFIELD))
    private int reserveTabSpace(int height) {
        return height + ric$verticalOffset() * 2;
    }

    @Inject(method = "initGui()V", at = @At("HEAD"))
    private void sizeAchievementWindow(CallbackInfo callback) {
        boolean firstInit = ric$pages == null;
        if (ric$pages == null) ric$pages = new AchievementPages(mc);
        ric$resizeWindow();
        if (firstInit) {
            double centerX = (AchievementList.minDisplayColumn + AchievementList.maxDisplayColumn) * 12.0 + 11;
            double centerY = (AchievementList.minDisplayRow + AchievementList.maxDisplayRow) * 12.0 + 11;
            ric$setNativeScroll(centerX - (achievementsPaneWidth - 32) / (2 * ric$nativeZoom),
                    centerY - (achievementsPaneHeight - 47) / (2 * ric$nativeZoom));
        }
    }

    @Inject(method = "initGui()V", at = @At("RETURN"))
    private void addAdvancementPages(CallbackInfo callback) {
        ric$pages.init((width - achievementsPaneWidth) / 2, (height - achievementsPaneHeight) / 2 + ric$verticalOffset(),
                achievementsPaneWidth, achievementsPaneHeight, buttonList);
        ric$positionButtons();
    }

    @Inject(method = "updateViewModeButtonState()V", at = @At("RETURN"))
    private void layoutVisibleButtons(CallbackInfo callback) {
        ric$positionButtons();
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("HEAD"), cancellable = true)
    private void drawAdvancementPage(int mouseX, int mouseY, float partialTick, CallbackInfo callback) {
        long now = System.nanoTime();
        ric$pages.updateWindowSize(now);
        if (ric$resizeWindow()) {
            ric$pages.resize((width - achievementsPaneWidth) / 2, (height - achievementsPaneHeight) / 2 + ric$verticalOffset(),
                    achievementsPaneWidth, achievementsPaneHeight);
        }
        ric$pages.refresh();
        ric$pages.updateZoom(now);
        ric$updateNativeZoom(now);
        if (ric$pages.isVanillaPage()) {
            if (Mouse.isButtonDown(0)) ric$dragNativePage(mouseX, mouseY);
            else ric$nativeDragging = false;
            return;
        }
        ric$nativeDragging = false;
        if (Mouse.isButtonDown(0)) ric$pages.mouseDragged(mouseX, mouseY);
        else ric$pages.mouseReleased();
        button_cycle_view_mode.drawButton = false;
        ric$positionButtons();
        drawDefaultBackground();
        ric$pages.renderPage();
        super.drawScreen(mouseX, mouseY, partialTick);
        ric$pages.renderTabs(mouseX, mouseY);
        callback.cancel();
    }

    @ModifyExpressionValue(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z"))
    private boolean useScaledViewportDragging(boolean pressed) {
        return false;
    }

    @WrapOperation(method = "drawScreen(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiAchievements;genAchievementBackground(IIF)V"))
    private void renderScaledNativePage(GuiAchievements screen, int mouseX, int mouseY, float partialTick, Operation<Void> original) {
        ric$renderMouseX = mouseX;
        ric$renderMouseY = mouseY;
        boolean inside = ric$pages.inside(mouseX, mouseY);
        int x = inside ? ric$pages.contentLeft() + (int) Math.floor((mouseX - ric$pages.contentLeft()) / ric$nativeZoom + guiMapX - Math.floor(guiMapX)) : -1;
        int y = inside ? ric$pages.contentTop() + (int) Math.floor((mouseY - ric$pages.contentTop()) / ric$nativeZoom + guiMapY - Math.floor(guiMapY)) : -1;
        try {
            original.call(screen, x, y, partialTick);
        } finally {
            ric$endNativeTransform();
        }
    }

    @WrapOperation(method = "genAchievementBackground(IIF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/Achievement;theItemStack:Lnet/minecraft/ItemStack;", opcode = Opcodes.GETFIELD))
    private ItemStack resolveNativeIcon(Achievement achievement, Operation<ItemStack> original) {
        return AdvancementIcons.nativeIcon(achievement, original.call(achievement));
    }

    @WrapOperation(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/RenderItem;renderItemAndEffectIntoGUI(Lnet/minecraft/FontRenderer;Lnet/minecraft/TextureManager;Lnet/minecraft/ItemStack;II)V"))
    private void renderNativeIcon(RenderItem renderer, FontRenderer font, TextureManager textures, ItemStack stack, int x, int y, Operation<Void> original) {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glPushMatrix();
        float previousZLevel = renderer.zLevel;
        try {
            renderer.zLevel = previousZLevel + 100.0F;
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(true);
            original.call(renderer, font, textures, stack, x, y);
        } finally {
            renderer.zLevel = previousZLevel;
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @WrapOperation(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V", ordinal = 0))
    private void scaleNativeTree(float x, float y, float z, Operation<Void> original) {
        original.call(x, y, z);
        ric$pages.drawContentBackground();
        int left = ric$pages.contentLeft(), top = ric$pages.contentTop();
        int scale = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight).getScaleFactor();
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glPushMatrix();
        ric$nativeTransform = true;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(left * scale, mc.displayHeight - (top + ric$pages.contentHeight()) * scale,
                ric$pages.contentWidth() * scale, ric$pages.contentHeight() * scale);
        GL11.glTranslatef(left + (float) ((Math.floor(guiMapX) - guiMapX) * ric$nativeZoom),
                top + (float) ((Math.floor(guiMapY) - guiMapY) * ric$nativeZoom), 0);
        GL11.glScalef((float) ric$nativeZoom, (float) ric$nativeZoom, 1);
        GL11.glTranslatef(-left, -top, 0);
    }

    @WrapOperation(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiAchievements;drawTexturedModalRect(IIIIII)V"))
    private void renderResizableFrame(GuiAchievements screen, int x, int y, int u, int v, int width, int height, Operation<Void> original) {
        if (u == 0 && v == 0 && width == achievementsPaneWidth && height == achievementsPaneHeight) {
            ric$endNativeTransform();
            ric$pages.drawFrame();
        } else {
            original.call(screen, x, y, u, v, width, height);
        }
    }

    @ModifyConstant(method = "genAchievementBackground(IIF)V", constant = @Constant(intValue = 224))
    private int nativeContentWidth(int width) {
        return (int) Math.ceil(ric$pages.contentWidth() / ric$nativeZoom) + 1;
    }

    @ModifyConstant(method = "genAchievementBackground(IIF)V", constant = @Constant(intValue = 155))
    private int nativeContentHeight(int height) {
        return (int) Math.ceil(ric$pages.contentHeight() / ric$nativeZoom) + 1;
    }

    @ModifyVariable(method = "genAchievementBackground(IIF)V", at = @At(value = "NEW", target = "java/util/Random"), name = "var12")
    private int wrapNativeBackgroundX(int offset) {
        return Math.floorMod(offset, 16);
    }

    @ModifyVariable(method = "genAchievementBackground(IIF)V", at = @At(value = "NEW", target = "java/util/Random"), name = "var13")
    private int wrapNativeBackgroundY(int offset) {
        return Math.floorMod(offset, 16);
    }

    @ModifyExpressionValue(method = {"drawScreen(IIF)V", "genAchievementBackground(IIF)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/GuiAchievements;guiMapTop:I", opcode = Opcodes.GETSTATIC))
    private int nativeMinimumX(int minimum) {
        return minimum + ric$nativeXOffset();
    }

    @ModifyExpressionValue(method = {"drawScreen(IIF)V", "genAchievementBackground(IIF)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/GuiAchievements;guiMapBottom:I", opcode = Opcodes.GETSTATIC))
    private int nativeMaximumX(int maximum) {
        return maximum + ric$nativeXOffset();
    }

    @ModifyExpressionValue(method = {"drawScreen(IIF)V", "genAchievementBackground(IIF)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/GuiAchievements;guiMapLeft:I", opcode = Opcodes.GETSTATIC))
    private int nativeMinimumY(int minimum) {
        return minimum + ric$nativeYOffset();
    }

    @ModifyExpressionValue(method = {"drawScreen(IIF)V", "genAchievementBackground(IIF)V"}, at = @At(value = "FIELD", target = "Lnet/minecraft/GuiAchievements;guiMapRight:I", opcode = Opcodes.GETSTATIC))
    private int nativeMaximumY(int maximum) {
        return maximum + ric$nativeYOffset();
    }

    @WrapOperation(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"))
    private int sampleExtendedBackground(Random random, int bound, Operation<Integer> original) {
        return original.call(random, Math.max(1, bound));
    }

    @ModifyVariable(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V"), argsOnly = true, ordinal = 0)
    private int restoreNativeMouseX(int mouseX) {
        return ric$renderMouseX;
    }

    @ModifyVariable(method = "genAchievementBackground(IIF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glPopMatrix()V"), argsOnly = true, ordinal = 1)
    private int restoreNativeMouseY(int mouseY) {
        return ric$renderMouseY;
    }

    @Inject(method = "drawScreen(IIF)V", at = @At("RETURN"))
    private void drawVanillaPageTabs(int mouseX, int mouseY, float partialTick, CallbackInfo callback) {
        if (ric$pages.isVanillaPage()) ric$pages.renderTabs(mouseX, mouseY);
    }

    @Inject(method = "actionPerformed(Lnet/minecraft/GuiButton;)V", at = @At("HEAD"), cancellable = true)
    private void switchAdvancementPages(GuiButton button, CallbackInfo callback) {
        if (ric$pages.buttonClicked(button)) callback.cancel();
    }

    @Inject(method = "keyTyped(CI)V", at = @At("HEAD"), cancellable = true)
    private void panAdvancementPage(char character, int key, CallbackInfo callback) {
        if (ric$pages.keyTyped(key)) callback.cancel();
    }

    @Inject(method = "doesGuiPauseGame()Z", at = @At("HEAD"), cancellable = true)
    private void receiveInitialAdvancements(CallbackInfoReturnable<Boolean> callback) {
        if (ClientAdvancements.isAwaitingSnapshot()) callback.setReturnValue(false);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        if (ric$pages.mouseClicked(x, y, button)) return;
        if (button == 0 && ric$pages.isVanillaPage() && ric$pages.inside(x, y)) {
            ric$nativeDragging = true;
            ric$dragX = x;
            ric$dragY = y;
        } else {
            super.mouseClicked(x, y, button);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
        if (button == 0) {
            ric$pages.mouseReleased();
            ric$nativeDragging = false;
        }
        super.mouseMovedOrUp(x, y, button);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int x = Mouse.getEventX() * width / mc.displayWidth;
        int y = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        int amount = Mouse.getEventDWheel();
        if (amount == 0) return;
        if (isCtrlKeyDown()) {
            ric$pages.adjustWindowSize(amount);
            return;
        }
        ric$pages.mouseScrolled(x, y, amount);
        if (ric$pages.isVanillaPage() && ric$pages.inside(x, y)) {
            ric$dragNativePage(x, y);
            ric$zoomAnchorX = (x - ric$pages.contentLeft()) / (double) ric$pages.contentWidth();
            ric$zoomAnchorY = (y - ric$pages.contentTop()) / (double) ric$pages.contentHeight();
            ric$nativeZoomTransition.animateTo(Math.max(0.5, Math.min(2,
                    Math.round((ric$nativeZoomTransition.target() + Integer.signum(amount) * 0.1) * 10) / 10.0)));
        }
    }
}
