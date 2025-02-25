package moddedmite.rustedironcore.mixin.block;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(BlockGravel.class)
public abstract class BlockGravelMixin extends BlockFalling {


    @Shadow
    public abstract boolean isNetherGravel(int metadata);

    public BlockGravelMixin(int par1, Material material, BlockConstants constants) {
        super(par1, material, constants);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public int dropBlockAsEntityItem(BlockBreakInfo info) {
        Random rand;
        if (info.getMetadata() == 1) {
            return super.dropBlockAsEntityItem(info);
        }
        if (info.wasExploded() || !info.wasHarvestedByPlayer()) {
            return super.dropBlockAsEntityItem(info);
        }
        int fortune = info.getHarvesterFortune();
        if (fortune > 3) {
            fortune = 3;
        }
        rand = info.world.rand;
        float dropAsGravelChance = Handlers.GravelDrop.onDropAsGravelChanceModify(info, 1.0F - (float) 3 / (12 - 2 * fortune));
        if (rand.nextFloat() < dropAsGravelChance) return super.dropBlockAsEntityItem(info);
        int id_dropped;
        float dropAsFlintChance = Handlers.GravelDrop.onDropAsFlintChanceModify(info, (float) 2 / 3);
        if (rand.nextFloat() < dropAsFlintChance) {
            float dropFlintAsChipChance = Handlers.GravelDrop.onDropFlintAsChipChanceModify(info, (float) 15 / 16);
            if (rand.nextFloat() < dropFlintAsChipChance) {
                if (info.wasExploded()) {
                    return super.dropBlockAsEntityItem(info);
                }
                id_dropped = Item.chipFlint.itemID;
            } else {
                id_dropped = info.wasExploded() ? Item.chipFlint.itemID : Item.flint.itemID;
            }
        } else {
            id_dropped = Handlers.GravelDrop.getRandomDropID(info, rand);
        }


//        else if (rand.nextInt(3) > 0) {
//            id_dropped = Item.copperNugget.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = Item.silverNugget.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = Item.goldNugget.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = info.wasExploded() ? -1 : Item.shardObsidian.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = info.wasExploded() ? -1 : Item.shardEmerald.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = info.wasExploded() ? -1 : Item.shardDiamond.itemID;
//        } else if (rand.nextInt(3) > 0) {
//            id_dropped = Item.mithrilNugget.itemID;
//        } else {
//            id_dropped = Item.adamantiumNugget.itemID;
//        }

        if (this.isNetherGravel(info.getMetadata())) {
            id_dropped = Handlers.GravelDrop.onNetherGravelDropIDModify(info, id_dropped);
//            if (id_dropped != Item.copperNugget.itemID && id_dropped != Item.silverNugget.itemID && id_dropped != Item.mithrilNugget.itemID && id_dropped != Item.adamantiumNugget.itemID) {
//                if (id_dropped == Item.shardObsidian.itemID || id_dropped == Item.shardEmerald.itemID || id_dropped == Item.shardDiamond.itemID) {
//                    id_dropped = Item.shardNetherQuartz.itemID;
//                }
//            } else {
//                id_dropped = Item.goldNugget.itemID;
//            }
        }

        if (id_dropped != -1) {
            DedicatedServer.incrementTournamentScoringCounter(info.getResponsiblePlayer(), Item.getItem(id_dropped));
        }

        if (info.wasHarvestedByPlayer() && (id_dropped == Item.chipFlint.itemID || id_dropped == Item.flint.itemID)) {
            info.getResponsiblePlayer().triggerAchievement(AchievementList.flintFinder);
        }

        Handlers.GravelDrop.onDropResult(info, id_dropped);

        return this.dropBlockAsEntityItem(info, id_dropped);

    }
}
