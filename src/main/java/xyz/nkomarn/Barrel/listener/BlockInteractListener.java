package xyz.nkomarn.Barrel.listener;

import net.minecraft.server.v1_15_R1.BlockPosition;
import net.minecraft.server.v1_15_R1.TileEntityChest;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.gui.Preview;
import xyz.nkomarn.Barrel.model.Crate;

public class BlockInteractListener implements Listener {
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block != null && (block.getType().equals(Material.CHEST) || block.getType().equals(Material.ENDER_CHEST))) {
            for (Crate crate : Barrel.crates) {
                if (crate.getLocation().equals(block.getLocation())) {
                    event.setCancelled(true);
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        new Preview(crate, player);
                    } else {
                        if (crate.isKey(event.getPlayer().getInventory().getItemInMainHand())) {
                            event.getPlayer().getInventory().getItemInMainHand().subtract();
                            crate.giveRandomReward(event.getPlayer());
                            animateChest(block.getLocation());
                        } else {
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                            Vector vector = event.getPlayer().getLocation().toVector().subtract(block.getLocation()
                                    .toVector()).normalize().multiply(1).setY(1);

                            if (event.getPlayer().isInsideVehicle()) {
                                event.getPlayer().getVehicle().setVelocity(vector);
                            } else {
                                event.getPlayer().setVelocity(vector);
                            }
                        }
                    }
                }
            }
        }
    }

    private void animateChest(Location crateLocation) {
        World world = ((CraftWorld) crateLocation.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(crateLocation.getX(), crateLocation.getY(), crateLocation.getZ());
        world.playBlockAction(position, world.getType(position).getBlock(), 1, 1);
        Bukkit.getScheduler().runTaskLater(Barrel.getBarrel(), () -> world.playBlockAction(position, world.getType(position).getBlock(), 1, 0), 10);
        crateLocation.getWorld().playSound(crateLocation, Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.0f);
    }
}
