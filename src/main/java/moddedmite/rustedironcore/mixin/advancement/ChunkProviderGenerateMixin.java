package moddedmite.rustedironcore.mixin.advancement;

import moddedmite.rustedironcore.internal.advancement.AdvancementStructures;
import net.minecraft.ChunkProviderGenerate;
import net.minecraft.MapGenMineshaft;
import net.minecraft.MapGenScatteredFeature;
import net.minecraft.MapGenStronghold;
import net.minecraft.MapGenVillage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkProviderGenerate.class)
public class ChunkProviderGenerateMixin implements AdvancementStructures {

    @Shadow private MapGenStronghold strongholdGenerator;
    @Shadow private MapGenVillage villageGenerator;
    @Shadow private MapGenMineshaft mineshaftGenerator;
    @Shadow private MapGenScatteredFeature scatteredFeatureGenerator;

    @Override
    public boolean ric$containsStructure(String id, int x, int y, int z) {
        return switch (id) {
            case "minecraft:stronghold" -> strongholdGenerator.hasStructureAt(x, y, z);
            case "minecraft:village" -> villageGenerator.hasStructureAt(x, y, z);
            case "minecraft:mineshaft" -> mineshaftGenerator.hasStructureAt(x, y, z);
            case "ric:scattered_feature" -> scatteredFeatureGenerator.hasStructureAt(x, y, z);
            default -> false;
        };
    }
}
