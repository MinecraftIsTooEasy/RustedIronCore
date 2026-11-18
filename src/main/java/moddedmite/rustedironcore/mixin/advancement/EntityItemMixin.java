package moddedmite.rustedironcore.mixin.advancement;

import moddedmite.rustedironcore.internal.advancement.AdvancementActor;
import net.minecraft.EntityItem;
import net.minecraft.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public class EntityItemMixin implements AdvancementActor {

    @Unique private String ric$thrower = "";

    @Override
    public String ric$getAdvancementActor() {
        return ric$thrower;
    }

    @Override
    public void ric$setAdvancementActor(String name) {
        ric$thrower = name;
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void writeThrower(NBTTagCompound data, CallbackInfo callback) {
        if (!ric$thrower.isEmpty()) data.setString("ric:thrower", ric$thrower);
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void readThrower(NBTTagCompound data, CallbackInfo callback) {
        ric$thrower = data.getString("ric:thrower");
    }
}
