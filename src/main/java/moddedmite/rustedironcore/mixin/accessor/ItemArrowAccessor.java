package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.ItemArrow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemArrow.class)
public interface ItemArrowAccessor {

    @Mutable
    @Accessor("material_types")
    static void rustedIronCore$setMaterialTypes(Material[] materialTypes) {
        throw new AssertionError();
    }
}
