package moddedmite.rustedironcore.mixin.world.material;

import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Material.class)
public interface IMaterialMixin {
    @Accessor
    float getDurability();
}
