package com.firestartermc.crates.listener;

import com.firestartermc.crates.crate.Crate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

public class KeyPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!Crate.VALID_KEY_MATERIALS.contains(event.getItemInHand().getType())) {
            return;
        }

        var data = event.getItemInHand().getItemMeta().getPersistentDataContainer();

        if (data.has(Crate.KEY_CRATE_NBT, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
