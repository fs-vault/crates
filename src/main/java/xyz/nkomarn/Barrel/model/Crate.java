package xyz.nkomarn.Barrel.model;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Crate {
    private final String name, color;
    private final Location location;
    private final List<Reward> rewards;

    public Crate(String name, String color, Location location, List<Reward> rewards) {
        this.name = name;
        this.color = color;
        this.location = location;
        this.rewards = rewards;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<Reward> getRewards() {
        return this.rewards;
    }

    public boolean isKey(ItemStack key) {
        if (key == null) return false;
        net.minecraft.server.v1_15_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        if (nmsKey.hasTag() && nmsKey.getTag().hasKey("crate")) {
            return nmsKey.getTag().getString("crate").equals(name);
        }
        return false;
    }

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

    public void giveRandomReward(Player player) {
        double randomWeight = Math.random() * rewards.stream().mapToDouble(Reward::getChance).sum();
        double countWeight = 0.0;
        for (Reward reward : rewards) {
            countWeight += reward.getChance();
            if (countWeight >= randomWeight) {
                reward.giveReward(player);
                return;
            }
        }
    }
}
