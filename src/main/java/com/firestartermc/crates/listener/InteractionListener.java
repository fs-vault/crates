package com.firestartermc.crates.listener;

import com.firestartermc.crates.Crates;
import com.firestartermc.crates.animation.ScrollingAnimation;
import com.firestartermc.crates.crate.CratePreview;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class InteractionListener implements Listener {

    private final Crates plugin;

    public InteractionListener(@NotNull Crates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        var crate = plugin.registeredCrates().get(event.getClickedBlock());

        if (crate == null) {
            return;
        }

        event.setCancelled(true);
        var player = event.getPlayer();
        var item = event.getItem();

        if (item != null && crate.isKey(item)) {
            if (plugin.animationController().isBeingViewed(crate)) {
                return;
            }

            var reward = crate.selectRandomReward();
            var lightning = crate.name().equals("luminous"); // TODO dum lazy hack
            plugin.animationController().playAnimation(crate, new ScrollingAnimation(crate, player, reward, lightning));

            item.subtract();
            return;
        }

        new CratePreview(crate, player).open();
    }
}
