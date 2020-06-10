package xyz.nkomarn.Barrel.objects;

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

/**
 * Represents a reward that belongs to a crate.
 */
public class Reward {
    private final ItemStack item;
    private final double chance;
    private final List<String> commands, messages;

    /**
     * Defines a new reward.
     * @param item The ItemStack that represents the reward in the preview menu.
     * @param chance The chance for this reward to be picked.
     * @param enchanted Whether the reward should display an enchantment glint in the preview menu.
     * @param commands The commands which should be run when this reward is selected.
     * @param messages The messages which should be sent when this reward is selected.
     */
    public Reward(ItemStack item, double chance, boolean enchanted, List<String> commands, List<String> messages) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
        lore.add(ChatColor.GRAY + String.format("Chance: %s%%", chance));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        item.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        if (enchanted) {
            item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        }

        this.item = item;
        this.chance = chance;
        this.commands = commands;
        this.messages = messages;
    }

    /**
     * Returns the ItemStack which represents the reward in the preview menu.
     * @return The ItemStack which represents this reward.
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Returns the change for the reward to be picked.
     * @return The chance for this reward to be picked.
     */
    public double getChance() {
        return chance;
    }

    /**
     * Runs the actions associated with this reward.
     * @param player The player to use for the [player] placeholder.
     */
    public void runActions(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&d&lRewards: &7You received %s&7.", item.getItemMeta().getDisplayName()
        )));
        commands.forEach(command -> Bukkit.getScheduler().callSyncMethod(Barrel.getBarrel(), () ->
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("[player]", player.getName()))));
        messages.forEach(message -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
    }
}
