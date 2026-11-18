package moddedmite.rustedironcore.internal.advancement;

import net.minecraft.Entity;
import net.minecraft.EntityPlayer;
import net.minecraft.EnumConsciousState;
import net.minecraft.ItemStack;
import moddedmite.rustedironcore.api.advancement.CriteriaTriggers;

import java.util.ArrayList;
import java.util.List;

/** Transient movement and sleep transitions; no progress is stored here. */
public class AdvancementPlayerState {

    private boolean initialized;
    private boolean sleeping;
    private boolean riding;
    private boolean falling;
    private double previousX, previousY, previousZ;
    private double fallX, fallY, fallZ;
    private double travelX, travelY, travelZ;
    private boolean travelling;
    private boolean enteredNether;
    private ItemStack[] inventory;
    private boolean ridingInLava;
    private double lavaX, lavaY, lavaZ;
    private boolean explosionImpulse;
    private double impulseX, impulseY, impulseZ;
    private Entity explosionCause;

    public void tick(EntityPlayer player) {
        trackInventory(player);
        boolean asleep = player.conscious_state == EnumConsciousState.sleeping;
        if (asleep && !sleeping) CriteriaTriggers.SLEPT_IN_BED.trigger(player, json -> true);
        sleeping = asleep;
        Entity vehicle = player.ridingEntity;
        if (vehicle != null && !riding) CriteriaTriggers.START_RIDING.trigger(player, json -> true);
        riding = vehicle != null;
        if (vehicle != null && vehicle.handleLavaMovement()) {
            if (!ridingInLava) {
                lavaX = player.posX;
                lavaY = player.posY;
                lavaZ = player.posZ;
            } else CriteriaTriggers.travelled(CriteriaTriggers.RIDE_ENTITY_IN_LAVA, player, lavaX, lavaY, lavaZ);
            ridingInLava = true;
        } else ridingInLava = false;
        if (initialized && player.fallDistance > 0 && !falling) {
            fallX = previousX;
            fallY = previousY;
            fallZ = previousZ;
            falling = true;
            if (explosionImpulse && player.posY >= impulseY) {
                CriteriaTriggers.fellAfterExplosion(player, impulseX, impulseY, impulseZ, explosionCause);
                explosionImpulse = false;
            }
        }
        if (falling && player.onGround) {
            CriteriaTriggers.travelled(CriteriaTriggers.FALL_FROM_HEIGHT, player, fallX, fallY, fallZ);
            falling = false;
            explosionImpulse = false;
            explosionCause = null;
        } else if (falling && player.fallDistance == 0) falling = false;
        previousX = player.posX;
        previousY = player.posY;
        previousZ = player.posZ;
        initialized = true;
    }

    private void trackInventory(EntityPlayer player) {
        int size = player.inventory.getSizeInventory();
        boolean changed = inventory == null || inventory.length != size;
        if (changed) inventory = new ItemStack[size];
        List<ItemStack> changedItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!ItemStack.areItemStacksEqual(inventory[i], stack)) {
                inventory[i] = stack == null ? null : stack.copy();
                if (inventory[i] != null && inventory[i].stackSize > 0) changedItems.add(inventory[i]);
                changed = true;
            }
        }
        if (changed) CriteriaTriggers.inventoryChanged(player, changedItems);
    }

    public void explosionHit(EntityPlayer player, Entity cause) {
        explosionImpulse = true;
        impulseX = player.posX;
        impulseY = player.posY;
        impulseZ = player.posZ;
        explosionCause = cause;
    }

    public void beforeTravel(EntityPlayer player) {
        if (player.dimension == 0) {
            travelX = player.posX;
            travelY = player.posY;
            travelZ = player.posZ;
        }
        travelling = true;
        falling = false;
        explosionImpulse = false;
        explosionCause = null;
        ridingInLava = false;
    }

    public void afterTravel(EntityPlayer player, int from, int to) {
        if (travelling && enteredNether && from == -1 && to == 0) CriteriaTriggers.travelled(CriteriaTriggers.NETHER_TRAVEL, player, travelX, travelY, travelZ);
        enteredNether = travelling && from == 0 && to == -1;
        travelling = false;
        initialized = false;
    }
}
