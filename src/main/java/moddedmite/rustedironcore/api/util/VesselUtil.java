package moddedmite.rustedironcore.api.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.ItemBowl;
import net.minecraft.ItemVessel;
import net.minecraft.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VesselUtil {
    // vessel, content, instance
    private static final Table<Material, Material, ItemVessel> BucketTable = HashBasedTable.create();
    // vessel, instance
    private static final Map<Material, ItemVessel> EmptyBucketMap = new HashMap<>();

    public static Optional<ItemVessel> getBucket(Material vessel_material, Material contents) {
        if (contents == null) {
            return Optional.ofNullable(EmptyBucketMap.get(vessel_material));
        }
        return Optional.ofNullable(BucketTable.get(vessel_material, contents));
    }

    public static void registerBucket(Material vessel_material, Material contents, ItemVessel itemBucket) {
        if (contents == null) {
            EmptyBucketMap.put(vessel_material, itemBucket);
        } else {
            BucketTable.put(vessel_material, contents, itemBucket);
        }
    }


    // vessel, content, instance
    private static final Table<Material, Material, ItemBowl> BowlTable = HashBasedTable.create();
    // vessel, instance
    private static final Map<Material, ItemBowl> EmptyBowlMap = new HashMap<>();

    public static Optional<ItemBowl> getBowl(Material vessel_material, Material contents) {
        if (contents == null) {
            return Optional.ofNullable(EmptyBowlMap.get(vessel_material));
        }
        return Optional.ofNullable(BowlTable.get(vessel_material, contents));
    }

    public static void registerBowl(ItemBowl itemBowl) {
        registerBowl(itemBowl.getVesselMaterial(), itemBowl.getContents(), itemBowl);
    }

    public static void registerBowl(Material vessel_material, Material contents, ItemBowl itemBowl) {
        if (contents == null) {
            EmptyBowlMap.put(vessel_material, itemBowl);
        } else {
            BowlTable.put(vessel_material, contents, itemBowl);
        }
    }
}
