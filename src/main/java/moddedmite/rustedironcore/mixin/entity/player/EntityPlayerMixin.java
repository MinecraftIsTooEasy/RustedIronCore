package moddedmite.rustedironcore.mixin.entity.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.block.Bed;
import moddedmite.rustedironcore.api.advancement.AdvancementPlayer;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.api.advancement.PlayerAdvancements;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.player.PlayerAPI;
import net.minecraft.Block;
import net.minecraft.ChunkCoordinates;
import net.minecraft.Damage;
import net.minecraft.Entity;
import net.minecraft.EntityDamageResult;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumEntityReachContext;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.EnumFace;
import net.minecraft.RaycastCollision;
import net.minecraft.NBTTagCompound;
import net.minecraft.Vec3;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements PlayerAPI, AdvancementPlayer {

    @Unique
    private final PlayerAdvancements ric$advancements = new PlayerAdvancements();

    @Override
    public PlayerAdvancements ric$getAdvancements() {
        return ric$advancements;
    }

    @WrapOperation(method = "onItemUseFinish()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/ItemStack;onItemUseFinish(Lnet/minecraft/World;Lnet/minecraft/EntityPlayer;)V"))
    private void trackConsumedItem(ItemStack stack, World world, EntityPlayer player, Operation<Void> original) {
        ItemStack consumed = stack.copy();
        original.call(stack, world, player);
        CriteriaTriggers.consumedItem(player, consumed);
    }

    @WrapOperation(method = "checkForBlockActivation", at = @At(value = "INVOKE", target = "Lnet/minecraft/Block;onBlockActivated(Lnet/minecraft/World;IIILnet/minecraft/EntityPlayer;Lnet/minecraft/EnumFace;FFF)Z"))
    private boolean trackBlockUse(Block block, World world, int x, int y, int z, EntityPlayer player, EnumFace face, float hitX, float hitY, float hitZ, Operation<Boolean> original) {
        boolean used = original.call(block, world, x, y, z, player, face, hitX, hitY, hitZ);
        if (used) CriteriaTriggers.defaultBlockUse(player, world, x, y, z);
        return used;
    }

    @WrapOperation(method = "onPlayerRightClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/Item;onItemRightClick(Lnet/minecraft/EntityPlayer;FZ)Z"))
    private boolean trackItemUsedOnBlock(Item item, EntityPlayer player, float partialTick, boolean control, Operation<Boolean> original, @Local(argsOnly = true) RaycastCollision hit) {
        ItemStack held = player.getHeldItemStack();
        ItemStack used = held == null ? null : held.copy();
        boolean result = original.call(item, player, partialTick, control);
        if (result && hit != null && hit.isBlock()) CriteriaTriggers.itemUsedOnBlock(player, player.worldObj, hit.block_hit_x, hit.block_hit_y, hit.block_hit_z, used);
        return result;
    }

    @Unique
    private boolean firstLogin = true;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @WrapOperation(method = {"inBed()Z", "getOutOfBed(Lnet/minecraft/Entity;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/World;getBlockId(III)I"))
    private int recognizeBed(World world, int x, int y, int z, Operation<Integer> original) {
        return ric$getBedId(world, x, y, z, original.call(world, x, y, z));
    }

    @WrapOperation(method = "verifyRespawnCoordinates(Lnet/minecraft/World;Lnet/minecraft/ChunkCoordinates;Z)Lnet/minecraft/ChunkCoordinates;", at = @At(value = "INVOKE", target = "Lnet/minecraft/World;getBlockId(III)I"))
    private static int recognizeRespawnBed(World world, int x, int y, int z, Operation<Integer> original) {
        return ric$getBedId(world, x, y, z, original.call(world, x, y, z));
    }

    @Unique
    private static int ric$getBedId(World world, int x, int y, int z, int id) {
        Block block = Block.blocksList[id];
        return block instanceof Bed bed && bed.isBed(world, x, y, z) ? Block.bed.blockID : id;
    }

    @WrapOperation(method = {"getIntoBed(IIII)V", "getOutOfBed(Lnet/minecraft/Entity;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockBed;setBedOccupied(Lnet/minecraft/World;IIIZ)V"))
    private void updateBedOccupancy(World world, int x, int y, int z, boolean occupied, Operation<Void> original) {
        Bed bed = Bed.getBed(world, x, y, z);
        if (bed == null) original.call(world, x, y, z, occupied);
        else bed.setBedOccupied(world, x, y, z, (EntityPlayer) (Object) this, occupied);
    }

    @WrapOperation(method = "getOutOfBed(Lnet/minecraft/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockBed;getNearestEmptyChunkCoordinates(Lnet/minecraft/World;IIIILnet/minecraft/Vec3;)Lnet/minecraft/ChunkCoordinates;"))
    private ChunkCoordinates findBedExit(World world, int x, int y, int z, int skipped, Vec3 preferredPosition, Operation<ChunkCoordinates> original) {
        return ric$getBedSpawnPosition(world, x, y, z, skipped, preferredPosition, original);
    }

    @WrapOperation(method = "verifyRespawnCoordinates(Lnet/minecraft/World;Lnet/minecraft/ChunkCoordinates;Z)Lnet/minecraft/ChunkCoordinates;", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockBed;getNearestEmptyChunkCoordinates(Lnet/minecraft/World;IIIILnet/minecraft/Vec3;)Lnet/minecraft/ChunkCoordinates;"))
    private static ChunkCoordinates findBedRespawn(World world, int x, int y, int z, int skipped, Vec3 preferredPosition, Operation<ChunkCoordinates> original) {
        return ric$getBedSpawnPosition(world, x, y, z, skipped, preferredPosition, original);
    }

    @Unique
    private static ChunkCoordinates ric$getBedSpawnPosition(World world, int x, int y, int z, int skipped, Vec3 preferredPosition, Operation<ChunkCoordinates> original) {
        Bed bed = Bed.getBed(world, x, y, z);
        return bed == null ? original.call(world, x, y, z, skipped, preferredPosition)
                : bed.getBedSpawnPosition(world, x, y, z, skipped, preferredPosition);
    }

    @ModifyExpressionValue(method = "getBedOrientationInDegrees()F", at = @At(value = "INVOKE", target = "Lnet/minecraft/BlockBed;j(I)I"))
    private int resolveBedOrientation(int original) {
        EntityPlayer player = (EntityPlayer) (Object) this;
        ChunkCoordinates position = player.bed_location;
        Bed bed = Bed.getBed(worldObj, position.posX, position.posY, position.posZ);
        return bed == null ? original : bed.getBedDirection(worldObj, position.posX, position.posY, position.posZ);
    }

    @Inject(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityLivingBase;onLivingUpdate()V", shift = At.Shift.AFTER))
    private void injectTick(CallbackInfo ci) {
        Handlers.Tick.onEntityPlayerTick((EntityPlayer) (Object) this);
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void write(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setBoolean("FirstLogin", this.firstLogin);
        par1NBTTagCompound.setCompoundTag("ric:advancements", ric$advancements.write());
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void read(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.firstLogin = par1NBTTagCompound.getBoolean("FirstLogin");
        ric$advancements.read(par1NBTTagCompound.getCompoundTag("ric:advancements"));
    }

    @Inject(method = "clonePlayer", at = @At("RETURN"))
    private void onClone(EntityPlayer par1EntityPlayer, boolean par2, CallbackInfo ci) {
        this.firstLogin = ((PlayerAPI) par1EntityPlayer).ric$IsFirstLogin();
        ric$advancements.read(((AdvancementPlayer) par1EntityPlayer).ric$getAdvancements().write());
    }

    @Override
    public boolean ric$IsFirstLogin() {
        return this.firstLogin;
    }

    @Override
    public void ric$SetFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    @Inject(method = "attackEntityFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/Damage;isExplosion()Z"))
    private void onPlayerReceiveDamageModify(Damage damage, CallbackInfoReturnable<EntityDamageResult> cir) {
        Handlers.Combat.onPlayerReceiveDamageModify((EntityPlayer) (Object) this, damage);
    }

    @ModifyReturnValue(method = "calcRawMeleeDamageVs(Lnet/minecraft/Entity;ZZ)F", at = @At(value = "RETURN", ordinal = 1))
    private float onPlayerRawMeleeDamageModify(float original, @Local(argsOnly = true) Entity target, @Local(argsOnly = true, ordinal = 0) boolean critical, @Local(argsOnly = true, ordinal = 1) boolean suspended_in_liquid) {
        return Handlers.Combat.onPlayerRawMeleeDamageModify((EntityPlayer) (Object) this, target, critical, suspended_in_liquid, original);
    }

    @ModifyReturnValue(method = "getReach(Lnet/minecraft/Block;I)F", at = @At("RETURN"))
    private float modifyReach(float original, @Local(argsOnly = true) Block block, @Local(argsOnly = true) int metadata) {
        return Handlers.Combat.onPlayerBlockReachModify((EntityPlayer) (Object) this, block, metadata, original);
    }

    @ModifyReturnValue(method = "getReach(Lnet/minecraft/EnumEntityReachContext;Lnet/minecraft/Entity;)F", at = @At("RETURN"))
    private float modifyReach(float original, @Local(argsOnly = true) EnumEntityReachContext context, @Local(argsOnly = true) Entity entity) {
        return Handlers.Combat.onPlayerEntityReachModify((EntityPlayer) (Object) this, context, entity, original);
    }

    @ModifyExpressionValue(method = "getCurrentPlayerStrVsBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/Item;getStrVsBlock(Lnet/minecraft/Block;I)F"))
    private float modifyRawStrVsBlock(float original, @Local Block block, @Local(ordinal = 3) int metadata, @Local Item held_item) {
        return Handlers.Combat.onPlayerRawStrVsBlockModify((EntityPlayer) (Object) this, held_item, block, metadata, original);
    }

    @ModifyArg(method = "getCurrentPlayerStrVsBlock", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(FF)F"), index = 0)
    private float modifyStrVsBlock(float str_vs_block) {
        return Handlers.Combat.onPlayerStrVsBlockModify((EntityPlayer) (Object) this, str_vs_block);
    }

    @Override
    public boolean knockBack(Entity attacker, float amount) {
        amount = Handlers.Combat.onPlayerReceiveKnockBackModify((EntityPlayer) (Object) this, attacker, amount);
        return super.knockBack(attacker, amount);
    }

    @ModifyReturnValue(method = "getHealthLimit()F", at = @At("RETURN"))
    private float modifyHealthLimit(float original) {
        return Handlers.PlayerAttribute.onHealthLimitModify((EntityPlayer) (Object) this, original);
    }

    @ModifyReturnValue(method = "getHighestPossibleLevel", at = @At("RETURN"))
    private static int modifyMaxLevel(int original) {
        return Handlers.PlayerAttribute.onLevelLimitModify(original);
    }

    @ModifyConstant(method = "getExperienceLevel(I)I", constant = @Constant(intValue = -40))
    private int modifyMinLevel(int original) {
        return Handlers.PlayerAttribute.onLevelMinLimitModify(original);
    }

    @ModifyConstant(method = "addExperience(IZZ)V", constant = @Constant(intValue = -40))
    private int modifyMinLevel_1(int original) {
        return Handlers.PlayerAttribute.onLevelMinLimitModify(original);
    }
}
