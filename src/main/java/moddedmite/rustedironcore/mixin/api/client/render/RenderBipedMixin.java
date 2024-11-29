package moddedmite.rustedironcore.mixin.api.client.render;

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

@Mixin(RenderBiped.class)
public abstract class RenderBipedMixin extends RenderLiving {
    @Shadow
    protected ModelBiped field_82425_h;

    @Shadow
    protected ModelBiped field_82423_g;

    @Shadow
    protected ModelBiped modelBipedMain;

    public RenderBipedMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Unique
    private List<ModelBiped> models;

    @Inject(method = "<init>(Lnet/minecraft/ModelBiped;FF)V", at = @At("RETURN"))
    private void onInit(ModelBiped par1ModelBiped, float par2, float par3, CallbackInfo ci) {
        this.models = new ArrayList<>();
        this.models.add(this.modelBipedMain);
        this.models.add(this.field_82423_g);
        this.models.add(this.field_82425_h);// in fact this is leggings
        Handlers.ArmorModel.onArmorModelRegister(this.models::add);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    protected int func_130006_a(EntityLiving player, int index, float partialTicks) {
        Item item;
        ItemStack itemStack = player.func_130225_q(3 - index);
        if (itemStack != null && (item = itemStack.getItem()) instanceof ItemArmor) {
            ItemArmor itemArmor = (ItemArmor) item;
            this.bindTexture(Handlers.ArmorModel.getArmorTexture(itemArmor, index).orElseGet(() -> RenderBiped.func_110857_a(itemArmor, index)));
            ModelBiped model = Handlers.ArmorModel.getArmorModel(itemArmor, index).orElse(index == 2 ? this.field_82425_h : this.field_82423_g);
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
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        float var10 = 1.0f;
        GL11.glColor3f(var10, var10, var10);
        ItemStack var11 = par1EntityLiving.getHeldItemStack();
        this.func_82420_a(par1EntityLiving, var11);
        double var12 = par4 - (double) par1EntityLiving.yOffset;
        if (par1EntityLiving.isSneaking()) {
            var12 -= 0.125;
        }
        super.doRenderLiving(par1EntityLiving, par2, var12, par6, par8, par9);
        this.models.forEach(x -> x.aimedBow = false);
        this.models.forEach(x -> x.isSneak = false);
        this.models.forEach(x -> x.heldItemRight = 0);
    }

    /**
     * @author Debris
     * @reason api
     */
    @Overwrite
    protected void func_82420_a(EntityLiving par1EntityLiving, ItemStack par2ItemStack) {
        this.modelBipedMain.heldItemRight = par2ItemStack != null ? 1 : 0;
        this.models.forEach(x -> x.heldItemRight = this.modelBipedMain.heldItemRight);
        this.models.forEach(x -> x.isSneak = par1EntityLiving.isSneaking());
    }
}
