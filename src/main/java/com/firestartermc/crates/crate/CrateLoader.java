package com.firestartermc.crates.crate;

import com.firestartermc.crates.action.CommandAction;
import com.firestartermc.crates.action.MessageAction;
import com.firestartermc.crates.action.RewardAction;
import com.firestartermc.kerosene.item.ItemBuilder;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrateLoader {

    @NotNull
    public static Map<Block, Crate> loadAllCrates(@NotNull Plugin plugin) {
        var directory = new File(plugin.getDataFolder(), "crates");

        if (!directory.exists()) {
            directory.mkdir();
        }

        return Arrays.stream(directory.listFiles())
                .map(YamlConfiguration::loadConfiguration)
                .map(CrateLoader::readCrate)
                .collect(Collectors.toMap(Crate::block, crate -> crate));
    }

    @NotNull
    private static Crate readCrate(@NotNull Configuration config) {
        var name = config.getString("name").toLowerCase();
        var displayName = MessageUtils.formatColors(config.getString("display-name"), true);
        var keyMaterial = Material.valueOf(config.getString("key-material"));
        var block = readBlock(config.getConfigurationSection("block"));
        var rewards = readRewards(config.getConfigurationSection("rewards"));

        return new Crate(name, displayName, block, rewards, 1, keyMaterial); // TODO items per roll
    }

    @NotNull
    private static Block readBlock(@NotNull ConfigurationSection section) {
        var world = Bukkit.getWorld(section.getString("world"));
        return world.getBlockAt(section.getInt("x"), section.getInt("y"), section.getInt("z"));
    }

    @NotNull
    private static List<Reward> readRewards(@NotNull ConfigurationSection section) {
        return section.getKeys(false).stream()
                .map(section::getConfigurationSection)
                .map(CrateLoader::readReward)
                .collect(Collectors.toList());
    }

    @NotNull
    private static Reward readReward(@NotNull ConfigurationSection section) {
        var chance = section.getDouble("chance");
        var item = readDisplayItem(section.getConfigurationSection("display"))
                .addLore("&r ", String.format("&fChance: &#96f1ff%s%%", chance))
                .build();
        var actions = readActions(section.getConfigurationSection("actions"));

        return new Reward(item, chance, actions);
    }

    @NotNull
    private static ItemBuilder readDisplayItem(@NotNull ConfigurationSection section) {
        var builder = ItemBuilder.of(Material.valueOf(section.getString("item")))
                .amount(section.getInt("amount", 1))
                .enchantUnsafe(Enchantment.MENDING, 1)
                .addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);

        if (section.contains("name")) {
            builder.name(section.getString("name"));
        }

        if (section.contains("lore")) {
            builder.lore(section.getStringList("lore"));
        }

        return builder;
    }

    @NotNull
    private static List<RewardAction> readActions(@NotNull ConfigurationSection section) {
        var actions = new ArrayList<RewardAction>();

        if (section.contains("commands")) {
            actions.add(new CommandAction(section.getStringList("commands")));
        }

        if (section.contains("messages")) {
            actions.add(new MessageAction(section.getStringList("messages")));
        }

        return actions;
    }
}
