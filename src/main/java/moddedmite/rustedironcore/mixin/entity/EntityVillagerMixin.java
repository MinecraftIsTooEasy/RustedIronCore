package moddedmite.rustedironcore.mixin.entity;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.events.TradingRegisterEvent;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.Map;
import java.util.Random;

@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable {
    @Shadow
    private MerchantRecipeList buyingList;

    @Shadow
    private float field_82191_bN;

    @Shadow
    private static void addMerchantItem(MerchantRecipeList par0MerchantRecipeList, int par1, Random par2Random, float par3) {
    }

    @Shadow
    public abstract void setProfession(int par1);

    @Shadow
    @Final
    private static Map<?, ?> blacksmithSellingList;

    @Shadow
    @Final
    private static Map<?, ?> villagerStockList;

    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onSpawnWithEgg", at = @At("RETURN"))
    private void onSpawnWithEgg(EntityLivingData par1EntityLivingData, CallbackInfoReturnable<EntityLivingData> cir) {
        this.setProfession(Handlers.Trading.getRandomProfession(this.rand));
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    private void addDefaultEquipmentAndRecipies(int unlockedRecipes) {// in mite it is 1
        if (this.buyingList != null) {
            this.field_82191_bN = MathHelper.sqrt_float((float) this.buyingList.size()) * 0.2F;
        } else {
            this.field_82191_bN = 0.0F;
        }

        MerchantRecipeList recipeList = new MerchantRecipeList();

        Handlers.Trading.addRecipeToList(recipeList, (EntityVillager) (Object) this, this.rand);

        if (recipeList.isEmpty()) {
            addMerchantItem(recipeList, Item.ingotGold.itemID, this.rand, 1.0F);
        }

        Collections.shuffle(recipeList);
        if (this.buyingList == null) {
            this.buyingList = new MerchantRecipeList();
        }

        for (int var9 = 0; var9 < unlockedRecipes && var9 < recipeList.size(); ++var9) {
            this.buyingList.addToListWithCheck((MerchantRecipe) recipeList.get(var9));
        }

    }

    @SuppressWarnings("unchecked")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        Handlers.Trading.publish(new TradingRegisterEvent((Map<Integer, Tuple>) villagerStockList, (Map<Integer, Tuple>) blacksmithSellingList));
    }
}
