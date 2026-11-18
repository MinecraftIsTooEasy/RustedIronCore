package moddedmite.rustedironcore.internal.advancement;

import moddedmite.rustedironcore.api.advancement.Advancements;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;
import moddedmite.rustedironcore.api.event.events.PlayerCrossDimensionEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedInEvent;
import moddedmite.rustedironcore.api.event.events.PlayerRespawnEvent;
import moddedmite.rustedironcore.api.event.events.PlayerLoggedOutEvent;
import moddedmite.rustedironcore.api.event.listener.IEntityEventListener;
import moddedmite.rustedironcore.api.event.listener.IAchievementListener;
import moddedmite.rustedironcore.api.event.listener.IInitializationListener;
import moddedmite.rustedironcore.api.event.listener.IPlayerEventListener;
import moddedmite.rustedironcore.api.event.listener.ITickListener;
import net.minecraft.EntityPlayer;
import net.minecraft.Entity;
import net.minecraft.EntityLivingBase;
import net.minecraft.DamageSource;
import net.minecraft.ItemStack;
import net.minecraft.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import java.util.Map;
import java.util.WeakHashMap;

public class AdvancementEvents implements IInitializationListener, IPlayerEventListener, ITickListener, IAchievementListener, IEntityEventListener {

    private static final Map<EntityPlayer, AdvancementPlayerState> STATES = new WeakHashMap<>();

    public static void explosionHit(EntityPlayer player, Entity cause) {
        if (player instanceof ServerPlayer) STATES.computeIfAbsent(player, ignored -> new AdvancementPlayerState()).explosionHit(player, cause);
    }

    public static void beforeDimensionChange(EntityPlayer player) {
        if (player instanceof ServerPlayer) STATES.computeIfAbsent(player, ignored -> new AdvancementPlayerState()).beforeTravel(player);
    }

    @Override
    public void onServerStarted(MinecraftServer server) {
        Advancements.reload();
    }

    @Override
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        AdvancementNetwork.sendSnapshot(event.player());
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        STATES.remove(event.player());
        AdvancementNetwork.sendSnapshot(event.player());
    }

    @Override
    public void onPlayerCrossDimension(PlayerCrossDimensionEvent event) {
        CriteriaTriggers.changedDimension(event.player(), event.from().id(), event.to().id());
        STATES.computeIfAbsent(event.player(), ignored -> new AdvancementPlayerState()).afterTravel(event.player(), event.from().id(), event.to().id());
        AdvancementNetwork.sendSnapshot(event.player());
    }

    @Override
    public void onEntityPlayerTick(EntityPlayer player) {
        if (!(player instanceof ServerPlayer)) return;
        CriteriaTriggers.tick(player);
        STATES.computeIfAbsent(player, ignored -> new AdvancementPlayerState()).tick(player);
    }

    @Override
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        STATES.remove(event.player());
    }

    @Override
    public void onDeath(EntityLivingBase entity, DamageSource source) {
        if (entity.worldObj.isRemote) return;
        Entity killer = source.getResponsibleEntity();
        if (killer instanceof ServerPlayer player) CriteriaTriggers.killed(player, entity, source, false);
        if (entity instanceof ServerPlayer player) CriteriaTriggers.killed(player, killer, source, true);
    }

    @Override
    public void onItemCrafted(EntityPlayer player, ItemStack stack) {
        CriteriaTriggers.inventoryChanged(player, stack);
    }

    @Override
    public void onItemPickUp(EntityPlayer player, ItemStack stack) {
        CriteriaTriggers.inventoryChanged(player, stack);
    }

    @Override
    public void onItemSmelt(EntityPlayer player, ItemStack stack) {
        CriteriaTriggers.inventoryChanged(player, stack);
    }
}
