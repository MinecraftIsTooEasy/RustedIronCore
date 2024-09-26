package moddedmite.rustedironcore.mixin.other.entity;

import moddedmite.rustedironcore.api.player.PlayerAPI;
import net.minecraft.EntityPlayer;
import net.minecraft.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin implements PlayerAPI {
    @Unique
    private boolean firstLogin = true;

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
        this.firstLogin = ((PlayerAPI) par1EntityPlayer).firstLogin();
    }

    @Override
    public boolean firstLogin() {
        return this.firstLogin;
    }

    @Override
    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}
