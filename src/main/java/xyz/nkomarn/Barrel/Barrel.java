package xyz.nkomarn.Barrel;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Barrel.command.GiveKeyCommand;
import xyz.nkomarn.Barrel.listener.BlockInteractListener;
import xyz.nkomarn.Barrel.model.Crate;
import xyz.nkomarn.Barrel.model.Reward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Barrel extends JavaPlugin {
    private static Barrel barrel;
    public static final Set<Crate> crates = new HashSet<>();

    public void onEnable() {
        barrel = this;
        saveDefaultConfig();
        loadCrates();

        getCommand("givekey").setExecutor(new GiveKeyCommand());

        getServer().getPluginManager().registerEvents(new BlockInteractListener(), this);
    }

    public static Barrel getBarrel() {
        return barrel;
    }

    private void loadCrates() {
        ConfigurationSection crates = getConfig().getConfigurationSection("crates");
        for (String crateName : crates.getKeys(false)) {
            ConfigurationSection crateSection = crates.getConfigurationSection(crateName);

            List<Reward> rewards = new ArrayList<>();

            ConfigurationSection locationSection = crateSection.getConfigurationSection("location");
            Location location = new Location(
                    Bukkit.getWorld(locationSection.getString("world")),
                    locationSection.getInt("x"),
                    locationSection.getInt("y"),
                    locationSection.getInt("z")
            );

            for (String rewardId : crateSection.getConfigurationSection("rewards").getKeys(false)) {
                ConfigurationSection rewardSection = crateSection.getConfigurationSection("rewards." + rewardId);
                ItemStack item = new ItemStack(
                        Material.getMaterial(rewardSection.getString("item.material", "BARRIER")),
                        rewardSection.getInt("item.amount", 1)
                );
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardSection.getString("item.name", "")));
                itemMeta.setLore(translateList(rewardSection.getStringList("item.lore")));
                item.setItemMeta(itemMeta);

                rewards.add(new Reward(
                        item,
                        rewardSection.getDouble("chance"),
                        rewardSection.getBoolean("enchanted"),
                        rewardSection.getStringList("commands").toArray(new String[0]),
                        rewardSection.getStringList("messages").toArray(new String[0])
                ));
            }
            Barrel.crates.add(new Crate(crateName, crateSection.getString("color"), location, rewards));
        }
    }

    private List<String> translateList(List<String> lore) {
        List<String> translatedLore = new ArrayList<>();
        if (lore == null) return translatedLore;

        for(String s : lore) {
            translatedLore.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return translatedLore;
    }
}
