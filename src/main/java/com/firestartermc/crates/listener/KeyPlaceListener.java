package com.firestartermc.crates.listener;

import com.firestartermc.crates.Crates;
import com.firestartermc.crates.crate.Crate;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class KeyPlaceListener implements Listener {

    private final Set<Material> validMaterials;

    public KeyPlaceListener(@NotNull Crates plugin) {
        this.validMaterials = EnumSet.noneOf(Material.class);
        validMaterials.addAll(plugin.registeredCrates().values().stream()
                .map(Crate::keyMaterial)
                .collect(Collectors.toSet()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (!validMaterials.contains(event.getItemInHand().getType())) {
            return;
        }

        var data = event.getItemInHand().getItemMeta().getPersistentDataContainer();

        if (data.has(Crate.KEY_CRATE_NBT, PersistentDataType.STRING)) {
            event.setCancelled(true);
        }
    }
}
