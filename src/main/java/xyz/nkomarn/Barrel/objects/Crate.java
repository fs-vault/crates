package xyz.nkomarn.Barrel.objects;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Represents a crate with rewards and a location.
 */
public class Crate {
    private final String name, color;
    private final Location location;
    private final List<Reward> rewards;

    /**
     * Defines a new crate.
     * @param name The internal name of the crate.
     * @param color The color to use for messages with this crate.
     * @param location The physical block location of the crate.
     * @param rewards A list of rewards contained within the crate.
     */
    public Crate(String name, String color, Location location, List<Reward> rewards) {
        this.name = name;
        this.color = color;
        this.location = location;
        this.rewards = rewards;
    }

    /**
     * Returns the internal name of the crate.
     * @return The internal name of this crate.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the physical block location of the crate.
     * @return The block location of this crate.
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Returns an ordered list of all of the rewards contained within the crate.
     * @return All of the rewards in this crate.
     */
    public List<Reward> getRewards() {
        return this.rewards;
    }

    /**
     * Validates whether an ItemStack is a key for this crate.
     * @param key An ItemStack to validate.
     * @return Whether the ItemStack is a valid key.
     */
    public boolean isKey(ItemStack key) {
        if (key == null) return false;
        net.minecraft.server.v1_15_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        if (nmsKey.hasTag() && nmsKey.getTag().hasKey("crate")) {
            return nmsKey.getTag().getString("crate").equals(name);
        }
        return false;
    }

    /**
     * Adds valid crate keys to the specified player's inventory.
     * @param player The player to give the keys to.
     * @param amount The amount of keys.
     */
    public void giveKey(Player player, int amount) {
        ItemStack key = new ItemStack(Material.NETHER_STAR, amount);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', String.format(
                "%s%s Key", color, WordUtils.capitalize(name)
        )));
        keyMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&fRedeem at &a/warp crates")));
        key.setItemMeta(keyMeta);

        net.minecraft.server.v1_15_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        nmsKey.getOrCreateTag().setString("crate", name);

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), CraftItemStack.asBukkitCopy(nmsKey));
        } else {
            player.getInventory().addItem(CraftItemStack.asBukkitCopy(nmsKey));
        }
    }

    /**
     * Award the specified player with a randomly chosen reward from this crate.
     * @param player The player to give the reward to.
     */
    public void giveRandomReward(Player player) {
        double randomWeight = Math.random() * rewards.stream().mapToDouble(Reward::getChance).sum();
        double countWeight = 0.0;
        for (Reward reward : rewards) {
            countWeight += reward.getChance();
            if (countWeight >= randomWeight) {
                reward.runActions(player);
                return;
            }
        }
    }
}
