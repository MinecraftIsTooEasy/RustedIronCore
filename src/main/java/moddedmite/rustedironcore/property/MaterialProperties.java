package moddedmite.rustedironcore.property;

import huix.glacier.api.extension.material.IBucketMaterial;
import huix.glacier.api.extension.material.ICoinMaterial;
import huix.glacier.api.extension.material.IRepairableMaterial;
import huix.glacier.api.extension.material.IToolMaterial;
import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.ItemCoin;
import net.minecraft.ItemFishingRod;
import net.minecraft.Material;
import org.jetbrains.annotations.ApiStatus;

/**
 * Call them at the {@link Handlers#PropertiesRegistry}
 */
public class MaterialProperties {
    /**
     * Also use {@link IRepairableMaterial}
     */
    public static final ItemProperty<Material> RepairItem = ItemProperty.of("RepairItem", null);
    /**
     * Also use {@link IToolMaterial}
     */
    public static final FloatProperty<Material> HarvestEfficiency = FloatProperty.of("HarvestEfficiency", 0.0F);
    /**
     * Also use {@link ICoinMaterial}
     */
    public static final Property<Material, ItemCoin> PeerCoin = new Property<>("PeerCoin", ItemCoin.class, null);
    /**
     * Also use {@link ICoinMaterial}
     */
    public static final IntegerProperty<Material> PeerCoinXP = IntegerProperty.of("PeerCoinXP", 0);
    /**
     * Also use {@link IBucketMaterial}; There has a default chance, you don't need always register it
     */
    public static final FloatProperty<Material> BucketMeltingChance = FloatProperty.of("BucketMeltingChance", 0.0F);
    @ApiStatus.Internal
    public static final Property<Material, ItemFishingRod> PeerFishingRod = new Property<>("PeerFishingRod", ItemFishingRod.class, null);
}
