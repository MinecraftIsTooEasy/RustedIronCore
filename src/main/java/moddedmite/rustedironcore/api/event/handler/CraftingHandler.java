package moddedmite.rustedironcore.api.event.handler;

import moddedmite.rustedironcore.api.event.EventHandler;
import moddedmite.rustedironcore.api.event.events.CraftingRecipeRegisterEvent;
import moddedmite.rustedironcore.internal.unsafe.CraftingResultAccess;
import net.minecraft.*;

import java.util.HashMap;
import java.util.Map;

public class CraftingHandler extends EventHandler<CraftingRecipeRegisterEvent> {
    private final Map<Item, Material> RepairArmorMap = new HashMap<>();

    public void registerArmorRepairRecipe(Item repairItem, Material materialForArmor) {
        this.RepairArmorMap.put(repairItem, materialForArmor);
    }

    private static final CraftingResult WaitForNextRecipe = new CraftingResult(null, 0.0f, null, null);

    public CraftingResult repairArmor(EntityPlayer player, ItemStack var4, ItemStack var5) {
        for (Item item : this.RepairArmorMap.keySet()) {
            CraftingResult craftingResult = this.repairArmorInternal(player, var4, var5, item, this.RepairArmorMap.get(item));
            if (craftingResult == null) return null;// fail
            if (CraftingResultAccess.isRepair(craftingResult)) {
                return craftingResult;// success
            }
        }
        return WaitForNextRecipe;
    }

    // the crafting difficulty is x*y*2, where x is number of repair item used, y is the difficulty of it, the 2 is by avernite's setting
    private CraftingResult repairArmorInternal(EntityPlayer player, ItemStack var4, ItemStack var5, Item repair_item, Material material) {
        boolean canFix = var4.getItem() == repair_item || var5.getItem() == repair_item;
        boolean b1 = var4.getItem() instanceof ItemArmor armor4 && armor4.hasMaterial(material) && var4.stackSize == 1 && var4.isItemDamaged();
        boolean b2 = var5.getItem() instanceof ItemArmor armor5 && armor5.hasMaterial(material) && var5.stackSize == 1 && var5.isItemDamaged();
        if (canFix && (b1 || b2)) {
            ItemStack item_stack_armor;
            ItemStack item_stack_sinew;
            if (var4.getItem() == repair_item) {
                item_stack_sinew = var4;
                item_stack_armor = var5;
            } else {
                item_stack_sinew = var5;
                item_stack_armor = var4;
            }
            if (item_stack_armor.getItem().hasQuality() && player != null && item_stack_armor.getQuality().isHigherThan(player.getMaxCraftingQuality(item_stack_armor.getItem().getLowestCraftingDifficultyToProduce(), item_stack_armor.getItem(), item_stack_armor.getItem().getSkillsetsThatCanRepairThis()))) {
                return null;// fail
            }
            int damage = item_stack_armor.getItemDamage();
            int damage_repaired_per_sinew = item_stack_armor.getMaxDamage() / item_stack_armor.getItem().getRepairCost();
            int num_sinews_to_use = damage / damage_repaired_per_sinew;
            if (damage % damage_repaired_per_sinew != 0) {
                ++num_sinews_to_use;
            }
            if (num_sinews_to_use > 1 && num_sinews_to_use * damage_repaired_per_sinew > damage) {
                --num_sinews_to_use;
            }
            if (num_sinews_to_use > item_stack_sinew.stackSize) {
                num_sinews_to_use = item_stack_sinew.stackSize;
            }
            int damage_repaired = num_sinews_to_use * damage_repaired_per_sinew;
            int damage_after_repair = Math.max(damage - damage_repaired, 0);
            ItemStack resulting_stack = item_stack_armor.copy().setItemDamage(damage_after_repair);
            CraftingResult crafting_result = new CraftingResult(resulting_stack, num_sinews_to_use * repair_item.getCraftingDifficultyAsComponent(null) * 2, item_stack_armor.getItem().getSkillsetsThatCanRepairThis(), null).setExperienceCostExempt().setQualityOverride(item_stack_armor.getQuality()).setConsumption(num_sinews_to_use);
            crafting_result.setRepair();
            return crafting_result;// success
        }
        return WaitForNextRecipe;
    }
}
