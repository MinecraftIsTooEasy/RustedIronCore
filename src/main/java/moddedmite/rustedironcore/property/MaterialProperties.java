package moddedmite.rustedironcore.property;

import net.minecraft.ItemCoin;
import net.minecraft.ItemFishingRod;
import net.minecraft.Material;

public class MaterialProperties {
    // call them at the Handlers#PropertiesRegistry
    @Deprecated(since = "1.3.5")
    public static final ItemProperty<Material> RepairItem = ItemProperty.of("RepairItem", null);
    @Deprecated(since = "1.3.5")
    public static final FloatProperty<Material> HarvestEfficiency = FloatProperty.of("HarvestEfficiency", 0.0F);
    @Deprecated(since = "1.3.5")
    public static final Property<Material, ItemCoin> PeerCoin = new Property<>("PeerCoin", ItemCoin.class, null);
    @Deprecated(since = "1.3.5")
    public static final IntegerProperty<Material> PeerCoinXP = IntegerProperty.of("PeerCoinXP", 0);
    // it has a default chance, you don't need always register it
    @Deprecated(since = "1.3.5")
    public static final FloatProperty<Material> BucketMeltingChance = FloatProperty.of("BucketMeltingChance", 0.0F);
    // internal api
    public static final Property<Material, ItemFishingRod> PeerFishingRod = new Property<>("PeerFishingRod", ItemFishingRod.class, null);
}
