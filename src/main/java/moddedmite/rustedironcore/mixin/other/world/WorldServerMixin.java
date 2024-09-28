package moddedmite.rustedironcore.mixin.other.world;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.handler.SpawnConditionHandler;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World {
    public WorldServerMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent, long world_creation_time, long total_world_time) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent, world_creation_time, total_world_time);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public Class getSuitableCreature(EnumCreatureType creature_type, int x, int y, int z) {
        boolean check_depth = this.isOverworld();
        boolean is_blood_moon_up = this.isBloodMoon(true);
        boolean is_freezing_biome = this.getBiomeGenForCoords(x, z).isFreezing();
        boolean is_desert_biome = this.getBiomeGenForCoords(x, z).isDesertBiome();
        boolean can_spawn_ghouls_on_surface = is_blood_moon_up;
        boolean can_spawn_wights_on_surface = is_blood_moon_up && is_freezing_biome;
        boolean can_spawn_shadows_on_surface = is_blood_moon_up && is_desert_biome;
        boolean can_spawn_revenants_on_surface = is_blood_moon_up;
        boolean can_spawn_bone_lords_on_surface = is_blood_moon_up;
        for (int attempt = 0; attempt < 16; ++attempt) {
            List possible_creatures = this.getChunkProvider().getPossibleCreatures(creature_type, x, y, z);
            if (possible_creatures == null || possible_creatures.isEmpty()) {
                return null;
            }
            SpawnListEntry entry = (SpawnListEntry) WeightedRandom.getRandomItem(this.rand, possible_creatures);
            Class entity_class = entry.entityClass;

            if (Handlers.SpawnCondition.has(entity_class)) {
                Class<? extends Entity> result = Handlers.SpawnCondition.get(entity_class).getResult(this, x, y, z);
                if (result == null) {
                    continue;
                }
                return result;
            }

            if (entity_class == EntityCreeper.class) {
                if (this.hasSkylight() && !this.isDaytime() && this.rand.nextInt(4) != 0 && this.isOutdoors(x, y, z))
                    continue;
                if (this.rand.nextInt(40) >= y && this.rand.nextFloat() < 0.5f) {
                    return EntityInfernalCreeper.class;
                }
            } else if (entity_class == EntitySlime.class) {
                if (this.blockTypeIsAbove(Block.stone, x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityGhoul.class) {
                if (check_depth && y > 56 && !can_spawn_ghouls_on_surface) {
                    continue;
                }
            } else if (entity_class == EntityJelly.class) {
                if (!this.blockTypeIsAbove(Block.stone, x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityWight.class) {
                if (check_depth && y > 48 && !can_spawn_wights_on_surface) {
                    continue;
                }
            } else if (entity_class == EntityVampireBat.class) {
                if (check_depth && y > 48 && !is_blood_moon_up) {
                    continue;
                }
            } else if (entity_class == EntityRevenant.class) {
                if (check_depth && y > 44 && !can_spawn_revenants_on_surface) {
                    continue;
                }
            } else if (entity_class == EntityInvisibleStalker.class) {
                if (check_depth && y > 40) {
                    continue;
                }
            } else if (entity_class == EntityEarthElemental.class) {
                if (check_depth && y > 40) {
                    continue;
                }
            } else if (entity_class == EntityBlob.class) {
                if (check_depth && y > 40 || !this.blockTypeIsAbove(Block.stone, x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityOoze.class) {
                if (check_depth && y > 32 || this.getBlock(x, y - 1, z) != Block.stone || !this.blockTypeIsAbove(Block.stone, x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityNightwing.class) {
                if (check_depth && y > 32 && !is_blood_moon_up) {
                    continue;
                }
            } else if (entity_class == EntityBoneLord.class) {
                if (check_depth && y > 32 && !can_spawn_bone_lords_on_surface) {
                    continue;
                }
            } else if (entity_class == EntityPudding.class) {
                if (check_depth && y > 24 || this.getBlock(x, y - 1, z) != Block.stone || !this.blockTypeIsAbove(Block.stone, x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityDemonSpider.class || entity_class == EntityPhaseSpider.class) {
                if (check_depth && y > 32) {
                    continue;
                }
            } else if (entity_class == EntityHellhound.class) {
                if (check_depth && y > 32) {
                    continue;
                }
            } else if (entity_class == EntityShadow.class) {
                if (check_depth && y > 32 && !can_spawn_shadows_on_surface) {
                    continue;
                }
            } else if (entity_class == EntitySpider.class) {
                if (this.hasSkylight() && this.rand.nextInt(4) == 0 && this.isOutdoors(x, y, z)) {
                    continue;
                }
            } else if (entity_class == EntityWoodSpider.class) {
                if (!((this.canBlockSeeTheSky(x, y, z) || this.blockTypeIsAbove((Block) Block.leaves, x, y, z) || this.blockTypeIsAbove((Block) Block.wood, x, y, z)) && this.blockTypeIsNearTo(Block.wood.blockID, x, y, z, 5, 2) && this.blockTypeIsNearTo(Block.leaves.blockID, x, y + 5, z, 5, 5))) {
                    continue;
                }
            } else if (entity_class == EntityBlackWidowSpider.class) {
                if (this.rand.nextFloat() < 0.5f) {
                    continue;
                }
            } else if (entity_class == EntityGhast.class) {
                for (Object object : this.loadedEntityList) {
                    Entity entity = (Entity) object;
                    if (!(entity instanceof EntityGhast) || !(entity.getDistanceSqToBlock(x, y, z) < 2304.0) || !(this.rand.nextFloat() < 0.8f))
                        continue;
                    entity_class = null;
                }
            }
            return entity_class;
        }
        return null;
    }
}
