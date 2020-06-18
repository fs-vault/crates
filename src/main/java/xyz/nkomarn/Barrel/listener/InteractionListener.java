package xyz.nkomarn.Barrel.listener;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.nkomarn.Barrel.Barrel;

/**
 * Handles player interactions with crate blocks.
 */
public class InteractionListener implements Listener {
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (!isCrate(block)) {
            return;
        }

        Barrel.getCrate(block)
                .ifPresent(crate -> {
                    event.setCancelled(true);

                    switch (event.getAction()) {
                        case LEFT_CLICK_BLOCK:
                            crate.openPreview(player);
                            break;
                        case RIGHT_CLICK_BLOCK:
                            if (!crate.isKey(player.getInventory().getItemInMainHand())) {
                                knockback(player, block.getLocation());
                                break;
                            }

                            animateCrate(block.getLocation());
                            if (player.isSneaking()) {
                                for (int i = 0; i < player.getInventory().getItemInMainHand().getAmount(); i++) {
                                    crate.giveReward(player);
                                }
                                player.getInventory().getItemInMainHand().setAmount(0);
                            } else {
                                player.getInventory().getItemInMainHand().subtract();
                                crate.giveReward(player);
                            }
                            break;
                    }
                });
    }

    /**
     * Returns whether the specified block is a valid crate block.
     *
     * @param block The block to run checks on.
     * @return Whether the specified block is a valid crate block.
     */
    private boolean isCrate(Block block) {
        return block != null && (block.getType().equals(Material.CHEST) || block.getType().equals(Material.ENDER_CHEST));
    }

    /**
     * Launch a player away from the crate.
     *
     * @param player        The player to launch.
     * @param crateLocation The location of the crate to launch from.
     */
    public void knockback(Player player, Location crateLocation) {
        Vector vector = player.getLocation().toVector().subtract(crateLocation.toVector()).normalize().multiply(1).setY(1);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

        if (player.isInsideVehicle()) {
            player.getVehicle().setVelocity(vector);
        } else {
            player.setVelocity(vector);
        }
    }

    /**
     * Play a chest open and close animation in the world.
     *
     * @param crateLocation The location of the crate to animate.
     */
    private void animateCrate(Location crateLocation) {
        World world = ((CraftWorld) crateLocation.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(crateLocation.getX(), crateLocation.getY(), crateLocation.getZ());
        world.playBlockAction(position, world.getType(position).getBlock(), 1, 1);
        Bukkit.getScheduler().runTaskLater(Barrel.getBarrel(), () -> world.playBlockAction(position,
                world.getType(position).getBlock(), 1, 0), 10);
        crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.0f);
    }
}
