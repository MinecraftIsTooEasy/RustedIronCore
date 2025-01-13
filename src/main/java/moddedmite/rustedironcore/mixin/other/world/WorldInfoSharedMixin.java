package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.NBTTagCompound;
import net.minecraft.WorldInfoShared;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfoShared.class)
public class WorldInfoSharedMixin {
    @Inject(method = "updateTagCompound", at = @At("RETURN"))
    private void onNBTWrite(NBTTagCompound par1NBTTagCompound, NBTTagCompound par2NBTTagCompound, CallbackInfo ci) {
        Handlers.WorldInfo.onNBTWrite(par1NBTTagCompound);
    }// the par2 is just player info

    @Inject(method = "<init>(Lnet/minecraft/NBTTagCompound;)V", at = @At("RETURN"))
    private void onNBTRead(NBTTagCompound par1NBTTagCompound, CallbackInfo ci) {
        Handlers.WorldInfo.onNBTRead(par1NBTTagCompound);
    }
}
