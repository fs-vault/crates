package xyz.nkomarn.Barrel.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.nkomarn.Barrel.Barrel;
import com.firestartermc.kerosene.util.PlayerUtils;
import com.firestartermc.kerosene.util.BlockUtils;

/**
 * Handles player interactions with crate blocks.
 */
public class InteractionListener implements Listener {

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var block = event.getClickedBlock();

        if (!isCrate(block)) {
            return;
        }

        var optional = Barrel.getCrate(block);
        if (optional.isEmpty()) {
            return;
        }

        var crate = optional.get();
        event.setCancelled(true);

        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> crate.openPreview(player);
            case RIGHT_CLICK_BLOCK -> {
                if (!crate.isKey(player.getInventory().getItemInMainHand())) {
                    PlayerUtils.pushAway(player, block.getLocation(), 1.0f, 1.0f);
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                    break;
                }

                block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0f, 1.0f);
                BlockUtils.animateChestAction(block, true);
                Bukkit.getScheduler().runTaskLater(Barrel.getBarrel(), () -> BlockUtils.animateChestAction(block, false), 10L);

                if (player.isSneaking()) {
                    for (int i = 0; i < player.getInventory().getItemInMainHand().getAmount(); i++) {
                        crate.giveReward(player);
                    }
                    player.getInventory().getItemInMainHand().setAmount(0);
                } else {
                    player.getInventory().getItemInMainHand().setAmount(
                            Math.max(0, player.getInventory().getItemInMainHand().getAmount() - 1)
                    );
                    crate.giveReward(player);
                }
            }
        }
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
}
