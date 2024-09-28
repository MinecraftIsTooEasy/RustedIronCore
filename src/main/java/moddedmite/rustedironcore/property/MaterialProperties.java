package moddedmite.rustedironcore.property;

import net.minecraft.ItemCoin;
import net.minecraft.ItemFishingRod;
import net.minecraft.Material;

public class MaterialProperties {
    // call them at the Handlers#PropertiesRegistry
    public static final ItemProperty<Material> RepairItem = ItemProperty.of("RepairItem", null);
    public static final FloatProperty<Material> HarvestEfficiency = FloatProperty.of("HarvestEfficiency", 0.0F);
    public static final Property<Material, ItemCoin> PeerCoin = new Property<>("PeerCoin", ItemCoin.class, null);
    public static final IntegerProperty<Material> PeerCoinXP = IntegerProperty.of("PeerCoinXP", 0);
    // it has a default chance, you don't need always register it
    public static final FloatProperty<Material> BucketMeltingChance = FloatProperty.of("BucketMeltingChance", 0.0F);
    public static final Property<Material, ItemFishingRod> PeerFishingRod = new Property<>("PeerFishingRod", ItemFishingRod.class, null);
}
