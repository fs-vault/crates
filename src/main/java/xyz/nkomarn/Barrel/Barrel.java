package xyz.nkomarn.Barrel;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Barrel.command.GiveKeyCommand;
import xyz.nkomarn.Barrel.listener.BlockInteractListener;
import xyz.nkomarn.Barrel.objects.Crate;
import xyz.nkomarn.Barrel.objects.Reward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Barrel extends JavaPlugin {
    private static Barrel barrel;
    private static final Set<Crate> crates = new HashSet<>();

    public void onEnable() {
        barrel = this;
        saveDefaultConfig();
        loadConfig();

        getCommand("givekey").setExecutor(new GiveKeyCommand());
        getServer().getPluginManager().registerEvents(new BlockInteractListener(), this);
    }

    /**
     * Fetches an instance of the Barrel plugin.
     * @return An instance of the Barrel plugin.
     */
    public static Barrel getBarrel() {
        return barrel;
    }

    /**
     * Fetches a set of all of the crates defined in the configuration.
     * @return A set of all of the crate objects loaded from the configuration.
     */
    public static Set<Crate> getCrates() {
        return crates;
    }

    /**
     * Load all crates defined in the configuration into memory.
     */
    private void loadConfig() {
        ConfigurationSection crates = getConfig().getConfigurationSection("crates");
        crates.getKeys(false).forEach(crateName -> {
            ConfigurationSection crateConfig = crates.getConfigurationSection(crateName);

            List<Reward> rewards = new ArrayList<>();
            crateConfig.getConfigurationSection("rewards").getKeys(false).stream().forEach(rewardId -> {
                ConfigurationSection rewardConfig = crateConfig.getConfigurationSection("rewards." + rewardId);
                Material rewardMaterial = Material.getMaterial(rewardConfig.getString("item.material", "BARRIER"));
                ItemStack rewardItem = new ItemStack(
                        rewardMaterial == null ? Material.BARRIER : rewardMaterial,
                        rewardConfig.getInt("item.amount", 1)
                );
                ItemMeta rewardMeta = rewardItem.getItemMeta();
                rewardMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', rewardConfig.getString("item.name", "")));
                rewardMeta.setLore(translateList(rewardConfig.getStringList("item.lore")));
                rewardItem.setItemMeta(rewardMeta);
                rewards.add(new Reward(
                        rewardItem,
                        rewardConfig.getDouble("chance"),
                        rewardConfig.getBoolean("enchanted"),
                        rewardConfig.getStringList("commands"),
                        rewardConfig.getStringList("messages")
                ));
            });

            ConfigurationSection locationConfig = crateConfig.getConfigurationSection("location");
            Location location = new Location(
                    Bukkit.getWorld(locationConfig.getString("world")),
                    locationConfig.getInt("x"),
                    locationConfig.getInt("y"),
                    locationConfig.getInt("z")
            );

            getCrates().add(new Crate(crateName, crateConfig.getString("color"), location, rewards));
        });
    }

    /**
     * Translate color codes in a string list.
     * @param list The string list with untranslated color codes.
     * @return The same string list with translated color codes.
     */
    private List<String> translateList(List<String> list) {
        List<String> translatedList = new ArrayList<>();
        if (list == null) return translatedList;
        list.forEach(entry -> translatedList.add(ChatColor.translateAlternateColorCodes('&', entry)));
        return translatedList;
    }
}
