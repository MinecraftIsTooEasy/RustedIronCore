package moddedmite.rustedironcore.mixin.client.render;

import moddedmite.rustedironcore.api.event.Handlers;
import net.minecraft.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin extends RendererLivingEntity {
    @Shadow
    private ModelBiped modelArmor;

    @Shadow
    private ModelBiped modelArmorChestplate;

    @Shadow
    private ModelBiped modelBipedMain;

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Unique
    private List<ModelBiped> models;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        this.models = new ArrayList<>();
        this.models.add(this.modelBipedMain);
        this.models.add(this.modelArmorChestplate);
        this.models.add(this.modelArmor);// in fact this is leggings
        Handlers.ArmorModel.onArmorModelRegister(this.models::add);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    protected int setArmorModel(AbstractClientPlayer player, int index, float partialTicks) {
        Item item;
        ItemStack itemStack = player.inventory.armorItemInSlot(3 - index);
        if (itemStack != null && (item = itemStack.getItem()) instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor) item;
            this.bindTexture(Handlers.ArmorModel.getArmorTexture(itemArmor, index).orElseGet(() -> RenderBiped.func_110857_a(itemArmor, index)));
            ModelBiped model = Handlers.ArmorModel.getArmorModel(itemArmor, index).orElse(index == 2 ? this.modelArmor : this.modelArmorChestplate);
            model.bipedHead.showModel = index == 0;
            model.bipedHeadwear.showModel = index == 0;
            model.bipedBody.showModel = index == 1 || index == 2;
            model.bipedRightArm.showModel = index == 1;
            model.bipedLeftArm.showModel = index == 1;
            model.bipedRightLeg.showModel = index == 2 || index == 3;
            model.bipedLeftLeg.showModel = index == 2 || index == 3;
            this.setRenderPassModel(model);
            model.onGround = this.mainModel.onGround;
            model.isRiding = this.mainModel.isRiding;
            model.isChild = this.mainModel.isChild;
            float var8 = 1.0f;
            if (itemArmor.getArmorMaterial() == Material.leather) {
                int var9 = itemArmor.getColor(itemStack);
                float var10 = (float) (var9 >> 16 & 0xFF) / 255.0f;
                float var11 = (float) (var9 >> 8 & 0xFF) / 255.0f;
                float var12 = (float) (var9 & 0xFF) / 255.0f;
                GL11.glColor3f(var8 * var10, var8 * var11, var8 * var12);
                if (itemStack.isItemEnchanted()) {
                    return 31;// leather dyed and enchant light
                }
                return 16;// leather dyed
            }
            GL11.glColor3f(var8, var8, var8);
            if (itemStack.isItemEnchanted()) {
                return 15;// enchant light
            }
            return 1;// common render
        }
        return -1;// skip render
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    public void func_130009_a(AbstractClientPlayer par1AbstractClientPlayer, double par2, double par4, double par6, float par8, float par9) {
        float var10 = 1.0f;
        GL11.glColor3f(var10, var10, var10);
        ItemStack var11 = par1AbstractClientPlayer.inventory.getCurrentItemStack();
        this.modelBipedMain.heldItemRight = var11 != null ? 1 : 0;
        this.models.forEach(x -> x.heldItemRight = this.modelBipedMain.heldItemRight);
        if (var11 != null && par1AbstractClientPlayer.getItemInUseCount() > 0) {
            EnumItemInUseAction var12 = var11.getItemInUseAction(par1AbstractClientPlayer);
            if (var12 == EnumItemInUseAction.BLOCK) {
                this.models.forEach(x -> x.heldItemRight = 3);
            } else if (var12 == EnumItemInUseAction.BOW) {
                this.models.forEach(x -> x.aimedBow = true);
            }
        }
        this.models.forEach(x -> x.isSneak = par1AbstractClientPlayer.isSneaking());
        double var14 = par4 - (double) par1AbstractClientPlayer.yOffset;
        if (par1AbstractClientPlayer.isSneaking() && !(par1AbstractClientPlayer instanceof ClientPlayer)) {
            var14 -= 0.125;
        }
        super.doRenderLiving(par1AbstractClientPlayer, par2, var14, par6, par8, par9);
        this.models.forEach(x -> x.aimedBow = false);
        this.models.forEach(x -> x.isSneak = false);
        this.models.forEach(x -> x.heldItemRight = 0);
    }
}
