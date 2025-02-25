package moddedmite.rustedironcore.mixin.tileEntity;

import huix.glacier.api.extension.item.IFusibleItem;
import huix.glacier.api.registry.MinecraftRegistry;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SmeltingHandler;
import moddedmite.rustedironcore.property.ItemProperties;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityFurnace.class)
public abstract class TileEntityFurnaceMixin extends TileEntity {
    @Shadow
    public abstract ItemStack getInputItemStack();

    @Shadow
    public int heat_level;

    @Shadow
    public int furnaceBurnTime;

    @Shadow
    public abstract boolean isFlooded();

    @Shadow
    public abstract boolean isSmotheredBySolidBlock();

    @Shadow
    protected abstract boolean canSmelt(int heat_level);

    @Shadow
    public abstract int getFuelHeatLevel();

    @Shadow
    public int currentItemBurnTime;

    @Shadow
    public abstract int getItemBurnTime(ItemStack item_stack);

    @Shadow
    private ItemStack[] furnaceItemStacks;

    @Shadow
    public abstract int getItemHeatLevel(ItemStack item_stack);

    @Shadow
    public abstract boolean isBurning();

    @Shadow
    public int furnaceCookTime;

    @Shadow
    public abstract void smeltItem(int heat_level);

    @ModifyConstant(method = "smeltItem", constant = @Constant(intValue = 1))
    private int modifyConsumption(int constant) {
        return Handlers.Smelting.match(getInputItemStack(), heat_level).map(SmeltingHandler.SmeltingResult::consumption).orElse(constant);
    }

    /**
     * @author Debris
     * @reason for at least some compatible
     */
    @Overwrite
    public int getCookProgressScaled(int par1) {
        return this.furnaceCookTime * par1 / Handlers.FurnaceUpdate.onFurnaceCookTimeTargetModify((TileEntityFurnace) (Object) this, 200);
    }

    /**
     * @author Debris
     * @reason for at least some compatible
     */
    @Overwrite
    public void updateEntity() {
        Handlers.FurnaceUpdate.onFurnaceUpdatePre((TileEntityFurnace) (Object) this);
        if (this.worldObj.isRemote || this.furnaceBurnTime == 1 || !this.isFlooded() && !this.isSmotheredBySolidBlock()) {
            boolean var1 = this.furnaceBurnTime > 0;
            boolean var2 = false;
            if (this.furnaceBurnTime > 0) {
                this.furnaceBurnTime -= Handlers.FurnaceUpdate.onFurnaceBurnTimeDecreaseModify((TileEntityFurnace) (Object) this, 1);
            } else {
                this.heat_level = 0;
            }

            if (!this.worldObj.isRemote) {
                if (this.furnaceBurnTime == 0 && this.canSmelt(this.getFuelHeatLevel()) && Handlers.FurnaceUpdate.onFurnaceBeginToBurn((TileEntityFurnace) (Object) this, true)) {
                    this.currentItemBurnTime = this.furnaceBurnTime = this.getItemBurnTime(this.furnaceItemStacks[1]);
                    if (this.furnaceBurnTime > 0) {
                        this.heat_level = this.getItemHeatLevel(this.furnaceItemStacks[1]);
                        var2 = true;
                        if (this.furnaceItemStacks[1] != null) {
                            --this.furnaceItemStacks[1].stackSize;
                            if (this.furnaceItemStacks[1].stackSize == 0) {
                                Item var3 = this.furnaceItemStacks[1].getItem().getContainerItem();
                                this.furnaceItemStacks[1] = var3 != null ? new ItemStack(var3) : null;
                            }
                            Handlers.FurnaceUpdate.onFurnaceFuelConsumed((TileEntityFurnace) (Object) this);
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt(this.heat_level)) {
                    Handlers.FurnaceUpdate.onFurnaceCookTimeAdd((TileEntityFurnace) (Object) this);
                    this.furnaceCookTime += Handlers.FurnaceUpdate.onFurnaceCookTimeIncreaseModify((TileEntityFurnace) (Object) this, 1);
                    if (this.furnaceCookTime >= Handlers.FurnaceUpdate.onFurnaceCookTimeTargetModify((TileEntityFurnace) (Object) this, 200)) {
                        this.furnaceCookTime = 0;
                        this.smeltItem(this.heat_level);
                        Handlers.FurnaceUpdate.onFurnaceCookSuccess((TileEntityFurnace) (Object) this);
                        var2 = true;
                    }
                } else {
                    this.furnaceCookTime = 0;
                }

                if (var1 != this.furnaceBurnTime > 0) {
                    var2 = true;
                    BlockFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
                }
            }

            if (var2) {
                this.onInventoryChanged();
            }

        } else {
            if (this.furnaceBurnTime > 0) {
                if (this.isFlooded()) {
                    this.worldObj.blockFX(EnumBlockFX.steam, this.xCoord, this.yCoord, this.zCoord);
                }

                BlockFurnace.updateFurnaceBlockState(false, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }

            this.furnaceBurnTime = 0;
            this.furnaceCookTime = 0;

        }
    }

    @Inject(method = "getHeatLevelRequired", at = @At("HEAD"), cancellable = true)
    private static void injectHeatLevel(int item_id, CallbackInfoReturnable<Integer> cir) {
        Item item = Item.getItem(item_id);
        IFusibleItem.cast(item).ifPresent(x -> cir.setReturnValue(x.getHeatLevelRequired()));
        if (MinecraftRegistry.instance != null && MinecraftRegistry.instance.itemHeatLevelMap.containsKey(item_id)) {
            cir.setReturnValue(MinecraftRegistry.instance.itemHeatLevelMap.get(item_id));
            return;
        }
        ItemProperties.HeatLevelRequired.getOptional(item).ifPresent(cir::setReturnValue);
    }
}
