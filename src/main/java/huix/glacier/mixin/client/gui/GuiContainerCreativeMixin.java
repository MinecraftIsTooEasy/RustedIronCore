package huix.glacier.mixin.client.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import huix.glacier.api.extension.creativetab.GlacierCreativeTabs;
import moddedmite.rustedironcore.api.util.StringUtil;
import moddedmite.rustedironcore.compat.PinyinHandler;
import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.util.ReflectHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Mixin( GuiContainerCreative.class )
public class GuiContainerCreativeMixin extends InventoryEffectRenderer {
    @Unique
    private static int tabPage = 0;
    @Unique
    private int maxPages = 0;

    @Overwrite
    public void initGui() {
        if (this.mc.playerController.isInCreativeMode()) {
            super.initGui();
            this.buttonList.clear();
            Keyboard.enableRepeatEvents(true);
            this.searchField = new GuiTextField(this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 89, this.fontRenderer.FONT_HEIGHT);
            this.searchField.setMaxStringLength(15);
            this.searchField.setEnableBackgroundDrawing(false);
            this.searchField.setVisible(false);
            this.searchField.setTextColor(16777215);
            int var1 = selectedTabIndex;
            selectedTabIndex = -1;
            this.setCurrentCreativeTab(GlacierCreativeTabs.newCreativeTabArray.get(var1));
            this.field_82324_x = new CreativeCrafting(this.mc);
            this.mc.thePlayer.inventoryContainer.addCraftingToCrafters(this.field_82324_x);
            int tabCount = GlacierCreativeTabs.newCreativeTabArray.size();
            if (tabCount > 12) {
                buttonList.add(new GuiButton(101, guiLeft,              guiTop - 50, 20, 20, "<"));
                buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
                maxPages = ((tabCount - 12) / 10) + 1;
            }
        } else {
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
    }

    @Overwrite
    protected void keyTyped(char par1, int par2) {
        if (!GlacierCreativeTabs.newCreativeTabArray.get(selectedTabIndex).hasSearchBar()) {
            if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat)) {
                this.setCurrentCreativeTab(CreativeTabs.tabAllSearch);
            } else {
                super.keyTyped(par1, par2);
            }
        } else {
            if (this.field_74234_w) {
                this.field_74234_w = false;
                this.searchField.setText("");
            }

            if (!this.checkHotbarKeys(par2)) {
                if (this.searchField.textboxKeyTyped(par1, par2)) {
                    this.updateCreativeSearch();
                } else {
                    super.keyTyped(par1, par2);
                }
            }
        }
    }

    @Overwrite
    private void updateCreativeSearch() {
        ContainerCreative var1 = (ContainerCreative)this.inventorySlots;
        var1.itemList.clear();
        CreativeTabs tab = GlacierCreativeTabs.newCreativeTabArray.get(selectedTabIndex);
        if (tab.hasSearchBar() && tab != CreativeTabs.tabAllSearch) {
            tab.displayAllReleventItems(var1.itemList);
            this.updateFilteredItems(var1);
            return;
        }

        Item[] var2 = Item.itemsList;
        int var3 = var2.length;

        int var4;
        for(var4 = 0; var4 < var3; ++var4) {
            Item var5 = var2[var4];
            if (var5 != null && var5.getCreativeTab() != null) {
                var5.getSubItems(var5.itemID, CreativeTabs.tabAllSearch, var1.itemList);
            }
        }

        Enchantment[] var8 = Enchantment.enchantmentsList;
        var3 = var8.length;

        for(var4 = 0; var4 < var3; ++var4) {
            Enchantment var12 = var8[var4];
            if (var12 != null) {
                Item.enchantedBook.func_92113_a(var12, var1.itemList);
            }
        }

        this.updateFilteredItems(var1);

    }

    @Unique
    private void updateFilteredItems(ContainerCreative containercreative) {
        Iterator var9 = containercreative.itemList.iterator();
        String var10 = this.searchField.getText().toLowerCase();

        while(var9.hasNext()) {
            ItemStack var11 = (ItemStack)var9.next();
            boolean var13 = false;

            for (Object o : var11.getTooltip(this.mc.thePlayer, true, null)) {
                String var7 = (String) o;
                PinyinHandler pinyinHandler = PinyinHandler.getInstance();
                if (var7.toLowerCase().contains(var10)) {
                    var13 = true;
                    break;
                }
                if (pinyinHandler.isValid() && pinyinHandler.contains(var7, var10)) {
                    var13 = true;
                    break;
                }
            }

            if (!var13) {
                var9.remove();
            }
        }

        this.currentScroll = 0.0F;
        containercreative.scrollTo(0.0F);
    }

    @Overwrite
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        CreativeTabs var3 = GlacierCreativeTabs.newCreativeTabArray.get(selectedTabIndex);
        if (var3 != null && var3.drawInForegroundOfTab()) {
            this.fontRenderer.drawString(I18n.getString(var3.getTranslatedTabLabel()), 8, 6, 4210752);
        }

    }

   @Overwrite
   protected void mouseClicked(int par1, int par2, int par3) {
       if (par3 == 0) {
           int var4 = par1 - this.guiLeft;
           int var5 = par2 - this.guiTop;

           for (CreativeTabs creativeTabs : GlacierCreativeTabs.newCreativeTabArray) {
               if (this.func_74232_a(creativeTabs, var4, var5)) {
                   return;
               }
           }
       }

       super.mouseClicked(par1, par2, par3);
   }

    @Overwrite
    protected void mouseMovedOrUp(int par1, int par2, int par3) {
        if (par3 == 0) {
            int var4 = par1 - this.guiLeft;
            int var5 = par2 - this.guiTop;

            for (CreativeTabs creativeTabs : GlacierCreativeTabs.newCreativeTabArray) {
                if (creativeTabs != null && this.func_74232_a(creativeTabs, var4, var5)) {
                    this.setCurrentCreativeTab(creativeTabs);
                    return;
                }
            }
        }

        super.mouseMovedOrUp(par1, par2, par3);
    }

    @Overwrite
    private boolean needsScrollBars() {
        if (GlacierCreativeTabs.newCreativeTabArray.get(selectedTabIndex) == null)
            return false;
        return selectedTabIndex != CreativeTabs.tabInventory.getTabIndex() && GlacierCreativeTabs.newCreativeTabArray.get(selectedTabIndex).shouldHidePlayerInventory()
                && ((ContainerCreative)this.inventorySlots).hasMoreThan1PageOfItemsInList();
    }

    @Overwrite
    private void setCurrentCreativeTab(CreativeTabs par1CreativeTabs) {
        int var2 = selectedTabIndex;
        selectedTabIndex = par1CreativeTabs.getTabIndex();
        ContainerCreative var3 = (ContainerCreative)this.inventorySlots;
        this.field_94077_p.clear();
        var3.itemList.clear();
        par1CreativeTabs.displayAllReleventItems(var3.itemList);
        if (par1CreativeTabs == CreativeTabs.tabInventory) {
            Container var4 = this.mc.thePlayer.inventoryContainer;
            if (this.backupContainerSlots == null) {
                this.backupContainerSlots = var3.inventorySlots;
            }

            var3.inventorySlots = new ArrayList();

            for(int var5 = 0; var5 < var4.inventorySlots.size(); ++var5) {
                SlotCreativeInventory var6 = new SlotCreativeInventory(ReflectHelper.dyCast(this), (Slot)var4.inventorySlots.get(var5), var5);
                var3.inventorySlots.add(var6);
                int var7;
                int var8;
                int var9;
                if (var5 >= 5 && var5 < 9) {
                    var7 = var5 - 5;
                    var8 = var7 / 2;
                    var9 = var7 % 2;
                    var6.xDisplayPosition = 9 + var8 * 54;
                    var6.yDisplayPosition = 6 + var9 * 27;
                } else if (var5 >= 0 && var5 < 5) {
                    var6.yDisplayPosition = -2000;
                    var6.xDisplayPosition = -2000;
                } else if (var5 < var4.inventorySlots.size()) {
                    var7 = var5 - 9;
                    var8 = var7 % 9;
                    var9 = var7 / 9;
                    var6.xDisplayPosition = 9 + var8 * 18;
                    if (var5 >= 36) {
                        var6.yDisplayPosition = 112;
                    } else {
                        var6.yDisplayPosition = 54 + var9 * 18;
                    }
                }
            }

            this.field_74235_v = new Slot(inventory, 0, 173, 112);
            var3.inventorySlots.add(this.field_74235_v);
        } else if (var2 == CreativeTabs.tabInventory.getTabIndex()) {
            var3.inventorySlots = this.backupContainerSlots;
            this.backupContainerSlots = null;
        }

        if (this.searchField != null) {
            if (par1CreativeTabs.hasSearchBar()) {
                this.searchField.setVisible(true);
                this.searchField.setCanLoseFocus(false);
                this.searchField.setFocused(true);
                this.searchField.setText("");
                this.updateCreativeSearch();
            } else {
                this.searchField.setVisible(false);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setFocused(false);
            }
        }

        this.currentScroll = 0.0F;
        var3.scrollTo(0.0F);
    }

    @Overwrite
    public void drawScreen(int par1, int par2, float par3) {
        boolean var4 = Mouse.isButtonDown(0);
        int var5 = this.guiLeft;
        int var6 = this.guiTop;
        int var7 = var5 + 175;
        int var8 = var6 + 18;
        int var9 = var7 + 14;
        int var10 = var8 + 112;
        if (!this.wasClicking && var4 && par1 >= var7 && par2 >= var8 && par1 < var9 && par2 < var10) {
            this.isScrolling = this.needsScrollBars();
        }

        if (!var4) {
            this.isScrolling = false;
        }

        this.wasClicking = var4;
        if (this.isScrolling) {
            this.currentScroll = ((float)(par2 - var8) - 7.5F) / ((float)(var10 - var8) - 15.0F);
            if (this.currentScroll < 0.0F) {
                this.currentScroll = 0.0F;
            }

            if (this.currentScroll > 1.0F) {
                this.currentScroll = 1.0F;
            }

            ((ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
        }

        super.drawScreen(par1, par2, par3);

        ArrayList<CreativeTabs> creativeTabArray = GlacierCreativeTabs.newCreativeTabArray;

        int start = tabPage * 10;
        int i2 = Math.min(creativeTabArray.size(), ((tabPage + 1) * 10) + 2);
        if (tabPage != 0) start += 2;
        boolean rendered = false;

        for (int i = start; i < i2; ++i) {
            CreativeTabs var14 = creativeTabArray.get(i);
            if (var14 != null && this.renderCreativeInventoryHoveringText(var14, par1, par2)) {
                rendered = true;
                break;
            }
        }

        if (!rendered && !renderCreativeInventoryHoveringText(CreativeTabs.tabAllSearch, par1, par2))
        {
            renderCreativeInventoryHoveringText(CreativeTabs.tabInventory, par1, par2);
        }

        if (this.field_74235_v != null && selectedTabIndex == CreativeTabs.tabInventory.getTabIndex() && this.isPointInRegion(this.field_74235_v.xDisplayPosition, this.field_74235_v.yDisplayPosition, 16, 16, par1, par2)) {
            this.drawCreativeTabHoveringText(I18n.getString("inventory.binSlot"), par1, par2);
        }

        if (maxPages != 0) {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = fontRenderer.getStringWidth(page);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            fontRenderer.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);
    }

    @Overwrite
    protected void drawItemStackTooltip(ItemStack par1ItemStack, int par2, int par3) {
        if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
            List var4 = par1ItemStack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips, (Slot)null);
            CreativeTabs var5 = par1ItemStack.getItem().getCreativeTab();
            if (var5 == null && par1ItemStack.itemID == Item.enchantedBook.itemID) {
                Map var6 = EnchantmentHelper.getStoredEnchantmentsMap(par1ItemStack);
                if (var6.size() == 1) {
                    Enchantment var7 = Enchantment.enchantmentsList[(Integer)var6.keySet().iterator().next()];
                    ArrayList<CreativeTabs> creativeTabArray = GlacierCreativeTabs.newCreativeTabArray;
                    for (CreativeTabs creativeTabs : creativeTabArray) {
                        if (var7.isOnCreativeTab(creativeTabs)) {
                            var5 = creativeTabs;
                            break;
                        }
                    }

                }
            }

            if (var5 != null) {
                var4.add(1, "" + EnumChatFormatting.BOLD + EnumChatFormatting.BLUE + I18n.getString(var5.getTranslatedTabLabel()));
            }

            for(int var12 = 0; var12 < var4.size(); ++var12) {
                if (var12 == 0) {
                    var4.set(var12, "§" + Integer.toHexString(par1ItemStack.getRarity().rarityColor) + (String)var4.get(var12));
                } else {
                    var4.set(var12, EnumChatFormatting.GRAY + (String)var4.get(var12));
                }
            }

            this.func_102021_a(var4, par2, par3);
        } else {
            super.drawItemStackTooltip(par1ItemStack, par2, par3);
        }

    }

    @Overwrite
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();

        ArrayList<CreativeTabs> creativeTabArray = GlacierCreativeTabs.newCreativeTabArray;
        CreativeTabs creativetabs = creativeTabArray.get(selectedTabIndex);

        int l;
        int start = tabPage * 10;
        int var6 = Math.min(creativeTabArray.size(), ((tabPage + 1) * 10 + 2));
        if (tabPage != 0) start += 2;

        for (l = start; l < var6; ++l) {
            CreativeTabs var8 = creativeTabArray.get(l);
            this.mc.getTextureManager().bindTexture(field_110424_t);
            if (var8 != null && var8.getTabIndex() != selectedTabIndex) {
                this.renderCreativeTab(var8);
            }
        }

        if (tabPage != 0)
        {
            if (creativetabs != CreativeTabs.tabAllSearch)
            {
                this.mc.getTextureManager().bindTexture(field_110424_t);
                renderCreativeTab(CreativeTabs.tabAllSearch);
            }
            if (creativetabs != CreativeTabs.tabInventory)
            {
                this.mc.getTextureManager().bindTexture(field_110424_t);
                renderCreativeTab(CreativeTabs.tabInventory);
            }
        }

        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + creativetabs.getBackgroundImageName()));
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.searchField.drawTextBox();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var9 = this.guiLeft + 175;
        var6 = this.guiTop + 18;
        l = var6 + 112;
        this.mc.getTextureManager().bindTexture(field_110424_t);
        if (creativetabs.shouldHidePlayerInventory()) {
            this.drawTexturedModalRect(var9, var6 + (int)((float)(l - var6 - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
        }

        if (creativetabs == null || creativetabs.getTabPage() != tabPage)
        {
            if (creativetabs != CreativeTabs.tabAllSearch && creativetabs != CreativeTabs.tabInventory)
            {
                return;
            }
        }

        this.renderCreativeTab(creativetabs);
        if (creativetabs == CreativeTabs.tabInventory) {
            GuiInventory.func_110423_a(this.guiLeft + 43, this.guiTop + 45, 20, (float)(this.guiLeft + 43 - par2), (float)(this.guiTop + 45 - 30 - par3), this.mc.thePlayer);
        }

    }

    @Inject(method = "func_74232_a", at = @At(value = "HEAD"), cancellable = true)
    private void injectFunc_74232_a(CreativeTabs par1CreativeTabs, int par2, int par3, CallbackInfoReturnable<Boolean> ci) {
        if (par1CreativeTabs.getTabPage() != tabPage) {
            if (par1CreativeTabs != CreativeTabs.tabAllSearch &&
                    par1CreativeTabs != CreativeTabs.tabInventory) {
                ci.setReturnValue(false);
            }
        }
    }

    @Inject(method = "renderCreativeTab", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V", ordinal = 0,shift = At.Shift.AFTER))
    private void injectResetColor(CreativeTabs par1CreativeTabs, CallbackInfo ci) {
        GL11.glColor3f(1F, 1F, 1F);
    }

    @Inject(method = "actionPerformed", at = @At(value = "RETURN"))
    protected void injectActionPerformed(GuiButton par1GuiButton, CallbackInfo ci) {
        if (par1GuiButton.id == 101) {
            tabPage = Math.max(tabPage - 1, 0);
        } else if (par1GuiButton.id == 102) {
            tabPage = Math.min(tabPage + 1, maxPages);
        }

    }

    public GuiContainerCreativeMixin(Container par1Container) {
        super(par1Container);
    }
    @Shadow
    private static InventoryBasic inventory;
    @Shadow
    private static int selectedTabIndex;
    @Shadow
    private float currentScroll;
    @Shadow
    private boolean isScrolling;
    @Shadow
    private boolean wasClicking;
    @Shadow
    private GuiTextField searchField;
    @Shadow
    private List backupContainerSlots;
    @Shadow
    private Slot field_74235_v;
    @Shadow
    private boolean field_74234_w;
    @Shadow
    private CreativeCrafting field_82324_x;
    @Shadow
    @Final
    private static ResourceLocation field_110424_t;
    @Shadow
    protected boolean renderCreativeInventoryHoveringText(CreativeTabs par1CreativeTabs, int par2, int par3) {
        return false;
    }
    @Shadow
    protected boolean func_74232_a(CreativeTabs par1CreativeTabs, int par2, int par3) {
        return false;
    }
    @Shadow
    protected void renderCreativeTab(CreativeTabs par1CreativeTabs) {
    }

}
