package xyz.nkomarn.Barrel.objects;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.event.CrateRewardEvent;
import xyz.nkomarn.Kerosene.menu.Menu;
import xyz.nkomarn.Kerosene.menu.MenuButton;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a crate with rewards and a location.
 */
public class Crate {
    private final String name, color;
    private final Block block;
    private final ArrayList<Reward> rewards;
    private final int prizes;

    /**
     * Defines a new crate.
     *
     * @param name    The internal name of the crate.
     * @param color   The color to use for messages with this crate.
     * @param block   The physical block location of the crate.
     * @param rewards A list of rewards contained within the crate.
     * @param prizes  The amount of prizes a player wins at once from the crate.
     */
    public Crate(String name, String color, Block block, ArrayList<Reward> rewards, int prizes) {
        this.name = name;
        this.color = color;
        this.block = block;
        this.rewards = rewards;
        this.prizes = prizes;
    }

    /**
     * Returns the internal name of the crate.
     *
     * @return The internal name of this crate.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the physical block location of the crate.
     *
     * @return The block location of this crate.
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * Returns an ordered list of all of the rewards contained within the crate.
     *
     * @return All of the rewards in this crate.
     */
    public List<Reward> getRewards() {
        return this.rewards;
    }

    /**
     * Validates whether an ItemStack is a key for this crate.
     *
     * @param key An ItemStack to validate.
     * @return Whether the ItemStack is a valid key.
     */
    public boolean isKey(ItemStack key) {
        if (key == null || !key.getType().equals(Material.CAMPFIRE)) return false;

        PersistentDataContainer container = key.getItemMeta().getPersistentDataContainer();
        if (container.has(Barrel.CRATE_NAMESPACE, PersistentDataType.STRING)) {
            return container.get(Barrel.CRATE_NAMESPACE, PersistentDataType.STRING).equals(name);
        }
        
        net.minecraft.server.v1_15_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        if (nmsKey.hasTag() && nmsKey.getTag().hasKey("crate")) {
            return nmsKey.getTag().getString("crate").equals(name);
        }
        return false;
    }

    /**
     * Adds valid crate keys to the specified player's inventory.
     *
     * @param player The player to give the keys to.
     * @param amount The amount of keys.
     */
    public void giveKey(Player player, int amount, boolean message) {
        ItemStack key = new ItemBuilder(Material.CAMPFIRE, amount)
                .name(String.format("%s%s Key", color, WordUtils.capitalize(name)))
                .lore("&7Redeem this key", "&7at /warp crates.")
                .enchantUnsafe(Enchantment.MENDING, 1)
                .build();

        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.getPersistentDataContainer().set(Barrel.CRATE_NAMESPACE, PersistentDataType.STRING, name);
        key.setItemMeta(keyMeta);
        key.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), key);
        } else {
            player.getInventory().addItem(key);
        }

        if (message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&6&lCrates: &7Received %sx %s key.", amount, WordUtils.capitalize(name)
            )));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);
        }
    }

    /**
     * Award the specified player with a randomly chosen reward from this crate.
     *
     * @param player The player to give the reward to.
     */
    public void giveReward(Player player) {
        for (int i = 0; i < prizes; i++) {
            double randomWeight = Math.random() * rewards.stream().mapToDouble(Reward::getChance).sum();
            double countWeight = 0.0;
            for (Reward reward : rewards) {
                countWeight += reward.getChance();
                if (countWeight >= randomWeight) {
                    reward.runActions(player);
                    Bukkit.getPluginManager().callEvent(new CrateRewardEvent(player, this, reward));
                    break;
                }
            }
        }
    }

    /**
     * Displays a crate rewards preview menu to a player.
     *
     * @param player The player for which to open the preview.
     */
    public void openPreview(Player player) {
        Menu preview = new Menu(player, WordUtils.capitalize(this.name) + " Crate", 27);
        AtomicInteger slot = new AtomicInteger(0);
        this.getRewards().forEach(reward -> preview.addButton(new MenuButton(
                preview,
                reward.getItem(),
                slot.getAndIncrement(),
                null
        )));
        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.0f);
        preview.open();
    }
}
