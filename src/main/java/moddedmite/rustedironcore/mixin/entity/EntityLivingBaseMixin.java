package moddedmite.rustedironcore.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.sound.BlockSoundType;
import moddedmite.rustedironcore.internal.entity.DeathExperienceBridge;
import net.minecraft.Block;
import net.minecraft.BlockBreakInfo;
import net.minecraft.BlockInfo;
import net.minecraft.Damage;
import net.minecraft.DamageSource;
import net.minecraft.Entity;
import net.minecraft.EntityArachnid;
import net.minecraft.EntityDamageResult;
import net.minecraft.EntityLivingBase;
import net.minecraft.EntityPlayer;
import net.minecraft.EntityWoodSpider;
import net.minecraft.Material;
import net.minecraft.NBTTagCompound;
import net.minecraft.Potion;
import net.minecraft.PotionEffect;
import net.minecraft.StepSound;
import net.minecraft.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class EntityLivingBaseMixin extends Entity implements DeathExperienceBridge {

    @Unique
    private final EntityLivingBase rusted_iron_core$instance = (EntityLivingBase) (Object) this;

    @Unique
    private boolean rusted_iron_core$deathExperienceConsumed;

    @Shadow
    public abstract PotionEffect getActivePotionEffect(Potion par1Potion);

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    public boolean has_taken_massive_fall_damage;

    @Shadow
    public abstract void makeSound(String sound, float volume_multiplier, float pitch_multiplier);

    @Shadow
    public abstract void makeSound(String sound);

    public EntityLivingBaseMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void rusted_iron_core$consumeDeathExperience() {
        this.rusted_iron_core$deathExperienceConsumed = true;
    }

    @Override
    public boolean rusted_iron_core$isDeathExperienceConsumed() {
        return this.rusted_iron_core$deathExperienceConsumed;
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    protected void fall(float fall_distance) {
        Handlers.EntityEvent.onFall(this.rusted_iron_core$instance, fall_distance);
        float damage;
        super.fall(fall_distance);
        if (this.worldObj.isRemote) {
            return;
        }
        if (fall_distance < 0.5f) {
            return;
        }
        PotionEffect var2 = this.getActivePotionEffect(Potion.jump);
        float var3 = var2 != null ? (float) (var2.getAmplifier() + 1) : 0.0f;
        float damage_before_cushioning = damage = fall_distance - 2.5f - var3;
        boolean playBlockFallSound = false;
        damage *= 2.0f;
        BlockInfo block_landed_on_info = this.getBlockRestingOn(0.1f);
        if (damage >= 1.0f) {
            int var5 = 0;
            if (block_landed_on_info != null && (var5 = block_landed_on_info.block.blockID) > 0) {
                Block block = Block.blocksList[var5];
                float cushioning = block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y, block_landed_on_info.z));
                if (block.blockMaterial == Material.snow || block.blockMaterial == Material.craftedSnow) {
                    block = this.worldObj.getBlock(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z);
                    cushioning = block == null ? (cushioning += 1.0f) : (cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z)));
                }
                if ((block = Block.blocksList[this.worldObj.getBlockId(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z)]) != null) {
                    cushioning += block.getCushioning(this.worldObj.getBlockMetadata(block_landed_on_info.x, block_landed_on_info.y + 1, block_landed_on_info.z));
                }
                if (cushioning > 1.0f) {
                    cushioning = 1.0f;
                }
                damage -= cushioning * 10.0f;
                damage *= 1.0f - cushioning;
            }
            if (this.rusted_iron_core$instance instanceof EntityArachnid) {
                damage *= this.rusted_iron_core$instance instanceof EntityWoodSpider ? 0.25f : 0.5f;
            }
            damage = Handlers.Combat.onEntityLivingFallDamageModify(
                    this.rusted_iron_core$instance, fall_distance, block_landed_on_info, damage);
            if (damage_before_cushioning > 4.0f && !this.worldObj.isRemote && block_landed_on_info != null && (block_landed_on_info.block == Block.glass || block_landed_on_info.block == Block.blockSnow && this.worldObj.isAirOrPassableBlock(block_landed_on_info.x, block_landed_on_info.y - 1, block_landed_on_info.z, true))) {
                this.worldObj.destroyBlock(new BlockBreakInfo(this.worldObj, block_landed_on_info.x, block_landed_on_info.y, block_landed_on_info.z).setCollidedWith(this), true);
                if (damage > 5.0f) {
                    damage = 5.0f;
                }
            }
            if (damage >= 1.0f) {
                if (damage >= this.getMaxHealth() * 0.5f) {
                    this.has_taken_massive_fall_damage = true;
                }
                this.makeSound(damage > 4.0f ? "damage.fallbig" : "damage.fallsmall");
                this.attackEntityFrom(new Damage(DamageSource.fall, damage));
                playBlockFallSound = true;
            }
        }
        if (block_landed_on_info != null && block_landed_on_info.block.stepSound != null) {
            StepSound stepSound = block_landed_on_info.block.stepSound;
            if (stepSound instanceof BlockSoundType soundType) {
                if (playBlockFallSound) {
                    this.playSound(soundType.getFallSound(), soundType.getVolume() * 0.5F, soundType.getPitch() * 0.75F);
                }
            } else if (this.rusted_iron_core$instance instanceof EntityPlayer) {
                this.makeSound(stepSound.getStepSound(), Math.min(fall_distance * 0.1f, 1.0f), 1.0f);
            } else {
                this.makeSound(stepSound.getStepSound(), Math.min(fall_distance * 0.2f, 2.0f), 1.0f);
            }
        }
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityLivingBase;dropContainedItems()V"))
    private void rusted_iron_core$onLoot(DamageSource par1DamageSource, CallbackInfo ci) {
        Handlers.EntityEvent.onLoot(this.rusted_iron_core$instance, par1DamageSource);
    }

    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    private void rusted_iron_core$onDeath(DamageSource par1DamageSource, CallbackInfo ci) {
        Handlers.EntityEvent.onDeath(this.rusted_iron_core$instance, par1DamageSource);
    }

    @ModifyExpressionValue(method = "onDeathUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/EntityLivingBase;getExperienceValue()I"))
    private int rusted_iron_core$readDeathExperience(int original) {
        return this.rusted_iron_core$deathExperienceConsumed ? 0 : original;
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void rusted_iron_core$writeDeathExperienceState(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        if (this.rusted_iron_core$deathExperienceConsumed) {
            par1NBTTagCompound.setBoolean("RICDeathExperienceConsumed", true);
        }
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void rusted_iron_core$readDeathExperienceState(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        this.rusted_iron_core$deathExperienceConsumed = par1NBTTagCompound.getBoolean("RICDeathExperienceConsumed");
    }

    @Inject(method = "attackEntityFrom", at = @At("HEAD"))
    private void rusted_iron_core$onAttackEntityFrom(Damage damage, CallbackInfoReturnable<EntityDamageResult> cir) {
        Handlers.EntityEvent.onAttackEntityFrom(this.rusted_iron_core$instance, damage);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void rusted_iron_core$onUpdate(CallbackInfo ci) {
        Handlers.EntityEvent.onUpdate(this.rusted_iron_core$instance);
    }

    @Inject(method = "jump", at = @At("TAIL"))
    private void rusted_iron_core$onJump(CallbackInfo ci) {
        Handlers.EntityEvent.onJump(this.rusted_iron_core$instance);
    }
}
