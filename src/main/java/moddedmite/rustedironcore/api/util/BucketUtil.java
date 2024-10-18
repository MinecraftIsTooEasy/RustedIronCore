package moddedmite.rustedironcore.api.util;

import net.minecraft.ItemBucket;
import net.minecraft.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BucketUtil {
    private static final Map<Material, Map<Material, ItemBucket>> BucketMap = new HashMap<>();

    public static Optional<ItemBucket> getBucket(Material vessel_material, Material contents) {
        return Optional.ofNullable(BucketMap.get(vessel_material)).map(x -> x.get(contents));
    }

    public static void register(ItemBucket itemBucket) {
        register(itemBucket.getVesselMaterial(), itemBucket.getContents(), itemBucket);
    }

    public static void register(Material vessel_material, Material contents, ItemBucket itemBucket) {
        if (BucketMap.containsKey(vessel_material)) {
            BucketMap.get(vessel_material).put(contents, itemBucket);
        } else {
            Map<Material, ItemBucket> innerMap = new HashMap<>();
            innerMap.put(contents, itemBucket);
            BucketMap.put(vessel_material, innerMap);
        }
    }
}
