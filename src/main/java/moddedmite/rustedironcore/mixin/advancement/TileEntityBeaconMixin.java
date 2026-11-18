package moddedmite.rustedironcore.mixin.advancement;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import net.minecraft.EntityPlayer;
import net.minecraft.TileEntityBeacon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityBeacon.class)
public class TileEntityBeaconMixin {

    @Inject(method = "updateState", at = @At("HEAD"))
    private void captureBeaconLevel(CallbackInfo callback, @Share("ric$beaconLevel") LocalIntRef level) {
        level.set(((TileEntityBeacon) (Object) this).getLevels());
    }

    @Inject(method = "updateState", at = @At("RETURN"))
    private void trackBeaconLevel(CallbackInfo callback, @Share("ric$beaconLevel") LocalIntRef level) {
        TileEntityBeacon beacon = (TileEntityBeacon) (Object) this;
        if (beacon.getWorldObj().isRemote || beacon.getLevels() <= level.get()) return;
        for (Object raw : beacon.getWorldObj().playerEntities) {
            EntityPlayer player = (EntityPlayer) raw;
            if (Math.abs(player.posX - (beacon.xCoord + 0.5)) <= 10 && Math.abs(player.posZ - (beacon.zCoord + 0.5)) <= 10
                    && player.posY >= beacon.yCoord - 5 && player.posY <= beacon.yCoord + 5)
                CriteriaTriggers.constructedBeacon(player, beacon.getLevels());
        }
    }
}
