package com.firestartermc.crates.listener;

import com.firestartermc.crates.Crates;
import com.firestartermc.crates.crate.CratePreview;
import com.firestartermc.kerosene.util.PlayerUtils;
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
        var item = event.getItem();

        if (item != null && crate.isKey(item)) {
            var reward = crate.selectRandomReward();
            reward.actions().forEach(action -> action.perform(reward, event.getPlayer()));
            // TODO do reward celebration or something idfk
            // TODO play an epic 1.17 sound or something
            return;
        }

        new CratePreview(crate, event.getPlayer()).open();
    }
}
