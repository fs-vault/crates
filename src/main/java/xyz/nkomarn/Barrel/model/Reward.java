package xyz.nkomarn.Barrel.model;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.nkomarn.Barrel.Barrel;

import java.util.ArrayList;
import java.util.List;

public class Reward {
    private final ItemStack item;
    private final double chance;
    private final String[] commands, messages;
    private final boolean awardItem;

    public Reward(ItemStack item, double chance, boolean enchanted, String[] commands, String[] messages, boolean awardItem) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = new ArrayList<>(itemMeta.getLore());
        lore.add(ChatColor.GRAY + String.format("Chance: %s%%", chance));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (enchanted) {
            item.addEnchantment(Enchantment.MENDING, 1);
        }

        this.item = item;
        this.chance = chance;
        this.commands = commands;
        this.messages = messages;
        this.awardItem = awardItem;
    }

    public ItemStack getItem() {
        return item;
    }

    public double getChance() {
        return chance;
    }

    public void award(Player player) {
        if (awardItem) {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
        }

        for (String command : commands) {
            Bukkit.getScheduler().callSyncMethod(Barrel.getBarrel(), () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            command.replace("[player]", player.getName())));
        }

        for (String message : messages) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }
}
