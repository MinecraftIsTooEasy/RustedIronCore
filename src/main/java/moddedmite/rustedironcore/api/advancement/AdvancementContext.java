package moddedmite.rustedironcore.api.advancement;

import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.ItemStack;
import net.minecraft.World;

import java.util.Random;

/** Native values available to advancement loot conditions. Entity and player may be absent. */
public record AdvancementContext(World world, EntityPlayer player, Entity entity, ItemStack tool, double x, double y, double z, Random random) {

    public AdvancementContext(World world, EntityPlayer player, Entity entity, ItemStack tool, double x, double y, double z) {
        this(world, player, entity, tool, x, y, z, world.rand);
    }

    public AdvancementContext withRandom(Random random) {
        return new AdvancementContext(world, player, entity, tool, x, y, z, random);
    }

    public static AdvancementContext of(EntityPlayer player, Entity entity) {
        return new AdvancementContext(player.worldObj, player, entity, player.getHeldItemStack(), player.posX, player.posY, player.posZ);
    }
}
