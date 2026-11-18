package moddedmite.rustedironcore.mixin.client;

import moddedmite.rustedironcore.api.block.PickBlock;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.model.JsonBlockModelManager;
import moddedmite.rustedironcore.api.sound.ResourceSoundPlayer;
import moddedmite.rustedironcore.internal.client.chat.ComponentTranslationManager;
import moddedmite.rustedironcore.internal.client.hud.ClientHud;
import moddedmite.rustedironcore.internal.client.advancement.ClientAdvancements;
import net.minecraft.IntegratedServer;
import net.minecraft.InventoryPlayer;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;
import net.minecraft.NetClientHandler;
import net.minecraft.RaycastCollision;
import net.minecraft.ReloadableResourceManager;
import net.minecraft.ResourceManager;
import net.minecraft.WorldClient;
import net.minecraft.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    public WorldClient theWorld;

    @Shadow
    public abstract NetClientHandler getNetHandler();

    @Shadow
    public abstract ResourceManager getResourceManager();

    @Shadow
    private IntegratedServer theIntegratedServer;

    @Inject(method = "startGame", at = @At("RETURN"))
    private void addGui(CallbackInfo ci) {
        Handlers.Initialization.onClientStarted(Minecraft.getMinecraft());
        ((ReloadableResourceManager) this.getResourceManager()).registerReloadListener(JsonBlockModelManager.INSTANCE);
        ((ReloadableResourceManager) this.getResourceManager()).registerReloadListener(ComponentTranslationManager.INSTANCE);
        ((ReloadableResourceManager) this.getResourceManager()).registerReloadListener(ResourceSoundPlayer::reload);
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        Handlers.Tick.onClientTick(Minecraft.getMinecraft());
    }

    @Inject(method = "clickMiddleMouseButton()V", at = @At("HEAD"), cancellable = true)
    private void pickCustomBlock(CallbackInfo callback) {
        Minecraft client = Minecraft.getMinecraft();
        RaycastCollision target = client.objectMouseOver;
        if (target == null || !target.isBlock()) return;
        if (!(target.world.getBlock(target.block_hit_x, target.block_hit_y, target.block_hit_z) instanceof PickBlock block)) return;
        callback.cancel();
        ItemStack picked = block.getPickBlock(target.world, target.block_hit_x, target.block_hit_y, target.block_hit_z, client.thePlayer);
        if (picked == null) return;

        InventoryPlayer inventory = client.thePlayer.inventory;
        boolean creative = client.thePlayer.capabilities.isCreativeMode;
        int matchingSlot = -1;
        for (int slot = 0; slot < InventoryPlayer.getHotbarSize(); slot++) {
            if (ItemStack.areItemStacksEqual(inventory.getStackInSlot(slot), picked, true)) {
                matchingSlot = slot;
                break;
            }
        }
        if (!creative && matchingSlot < 0) return;
        inventory.setCurrentItem(picked.itemID, picked.getItemSubtype(), picked.getHasSubtypes(), creative);
        if (matchingSlot >= 0) inventory.currentItem = matchingSlot;
        if (!creative) return;

        ItemStack selected = inventory.getCurrentItemStack();
        if (selected != null && selected.itemID == picked.itemID && selected.getItemSubtype() == picked.getItemSubtype()
                && !ItemStack.areItemStacksEqual(selected, picked, true)) {
            ItemStack replacement = picked.copy();
            replacement.stackSize = selected.stackSize;
            inventory.setInventorySlotContents(inventory.currentItem, replacement);
        }
        int slot = client.thePlayer.inventoryContainer.inventorySlots.size() - InventoryPlayer.getHotbarSize() + inventory.currentItem;
        client.playerController.sendSlotPacket(inventory.getCurrentItemStack(), slot);
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/GuiAchievement;updateAchievementWindow()V", shift = At.Shift.AFTER))
    private void renderModernToasts(CallbackInfo ci) {
        ClientHud.renderToasts();
    }

    @Inject(method = "launchIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/LoadingScreenRenderer;displayProgressMessage(Ljava/lang/String;)V"))
    private void onIntegratedServerLaunch(String par1Str, String par2Str, WorldSettings par3WorldSettings, CallbackInfo ci) {
        Handlers.Connection.onIntegratedConnection(this.getNetHandler(), this.theIntegratedServer);
    }

    @Inject(method = "loadWorld(Lnet/minecraft/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void onWorldLoadStart(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (par1WorldClient == null) {
            ClientHud.reset();
            ClientAdvancements.clear();
        }
        if (this.theWorld != null) {
            Handlers.WorldLoad.onWorldUnload(this.theWorld);
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/WorldClient;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onWorldLoadEnd(WorldClient par1WorldClient, String par2Str, CallbackInfo ci) {
        if (par1WorldClient != null) {
            Handlers.WorldLoad.onWorldLoad(par1WorldClient);
        }
    }
}
