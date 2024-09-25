package moddedmite.rustedironcore.property;

import net.minecraft.ItemCoin;
import net.minecraft.Material;

public class MaterialProperties {
    // should be called early, for example in the static block of your materials class
    public static final FloatProperty<Material> ArrowRecoveryChance = FloatProperty.of("ArrowRecoveryChance", 0.0F);
    public static final ItemProperty<Material> RepairItem = ItemProperty.of("RepairItem", null);
    public static final FloatProperty<Material> HarvestEfficiency = FloatProperty.of("HarvestEfficiency", 0.0F);
    public static final Property<Material, ItemCoin> PeerCoin = new Property<>("PeerCoin", ItemCoin.class, null);
    public static final IntegerProperty<Material> PeerCoinXP = IntegerProperty.of("PeerCoinXP", 0);
    // it has a default chance, you don't need always register it
    public static final FloatProperty<Material> BucketMeltingChance = FloatProperty.of("BucketMeltingChance", 0.0F);


}
