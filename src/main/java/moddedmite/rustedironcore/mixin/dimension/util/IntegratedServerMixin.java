package moddedmite.rustedironcore.mixin.dimension.util;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @ModifyConstant(method = "loadAllWorlds", constant = @Constant(intValue = 4))
    private int addDimensions(int constant) {
        return Handlers.Dimension.getCustomDimensionsCount() + constant;
    }
}
