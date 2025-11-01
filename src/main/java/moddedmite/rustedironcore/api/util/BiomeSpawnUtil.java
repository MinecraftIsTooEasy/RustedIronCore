package moddedmite.rustedironcore.api.util;

import net.minecraft.BiomeGenBase;
import net.minecraft.EntityLiving;
import net.minecraft.EnumCreatureType;
import net.minecraft.SpawnListEntry;

import java.util.List;

public class BiomeSpawnUtil {

    public static void addSpawn(Class<? extends EntityLiving> entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes) {
        for (BiomeGenBase biome : biomes) {
            @SuppressWarnings("unchecked")
            List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);
            for (SpawnListEntry entry : spawns) {
                if (entry.entityClass == entityClass) {
                    entry.itemWeight = weightedProb;
                    entry.minGroupCount = min;
                    entry.maxGroupCount = max;
                    break;
                }
            }
            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }

    public static void removeSpawn(Class<? extends EntityLiving> entityClass, EnumCreatureType typeOfCreature, BiomeGenBase... biomes) {
        for (BiomeGenBase biome : biomes) {
            biome.getSpawnableList(typeOfCreature).removeIf(entry -> ((SpawnListEntry) entry).entityClass == entityClass);
        }
    }

    public static void clearSpawn(EnumCreatureType typeOfCreature, BiomeGenBase... biomes) {
        for (BiomeGenBase biome : biomes) {
            biome.getSpawnableList(typeOfCreature).clear();
        }
    }
}
