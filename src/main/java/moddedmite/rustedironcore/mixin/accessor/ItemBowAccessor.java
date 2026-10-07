package moddedmite.rustedironcore.mixin.accessor;

import net.minecraft.ItemBow;
import net.minecraft.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemBow.class)
public interface ItemBowAccessor {

    @Accessor("possible_arrow_materials")
    static Material[] rustedIronCore$getPossibleArrowMaterials() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("possible_arrow_materials")
    static void rustedIronCore$setPossibleArrowMaterials(Material[] materials) {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("bow_pull_icon_name_array")
    static void rustedIronCore$setBowPullIconNameArray(String[] names) {
        throw new AssertionError();
    }
}
