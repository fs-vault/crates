package xyz.nkomarn.Barrel.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.model.Crate;

public class BlockInteractListener implements Listener {
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        Crate crate = Barrel.crates.get(0);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            crate.openPreview(event.getPlayer());
        }
    }
}
