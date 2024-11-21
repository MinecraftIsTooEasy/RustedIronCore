package moddedmite.rustedironcore.mixin.other.entity.player;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.player.PlayerAPI;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin extends EntityLivingBase implements PlayerAPI {
    @Unique
    private boolean firstLogin = true;

    public EntityPlayerMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onLivingUpdate()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityLivingBase;onLivingUpdate()V", shift = At.Shift.AFTER))
    private void injectTick(CallbackInfo ci) {
        Handlers.Tick.onEntityPlayerTick((EntityPlayer) (Object) this);
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void write(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        par1NBTTagCompound.setBoolean("FirstLogin", this.firstLogin);
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void read(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.firstLogin = par1NBTTagCompound.getBoolean("FirstLogin");
    }

    @Inject(method = "clonePlayer", at = @At("RETURN"))
    private void onClone(EntityPlayer par1EntityPlayer, boolean par2, CallbackInfo ci) {
        this.firstLogin = ((PlayerAPI) par1EntityPlayer).ric$IsFirstLogin();
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

}
