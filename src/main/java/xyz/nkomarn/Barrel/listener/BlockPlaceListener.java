package xyz.nkomarn.Barrel.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import xyz.nkomarn.Barrel.Barrel;

/**
 * Handles player block placements.
 * Cancels placement if the item is a crate key.
 */
public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.CAMPFIRE)) {
            Barrel.getCrates().forEach(crate -> {
                if (crate.isKey(event.getItemInHand())) {{
                    event.setCancelled(true);
                }}
            });
        }
    }
}
