package xyz.nkomarn.Barrel;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.Barrel.command.GiveKeyCommand;
import xyz.nkomarn.Barrel.command.KeyallCommand;
import xyz.nkomarn.Barrel.listener.BlockPlaceListener;
import xyz.nkomarn.Barrel.listener.InteractionListener;
import xyz.nkomarn.Barrel.objects.Crate;
import xyz.nkomarn.Barrel.objects.Reward;
import xyz.nkomarn.Kerosene.util.item.ItemBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Barrel extends JavaPlugin {
    private static Barrel barrel;
    private static final Set<Crate> crates = new HashSet<>();

    public void onEnable() {
        barrel = this;
        saveDefaultConfig();
        loadConfig();

        getCommand("givekey").setExecutor(new GiveKeyCommand());
        getCommand("keyall").setExecutor(new KeyallCommand());
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(), this);
    }

    /**
     * Fetches an instance of the Barrel plugin.
     *
     * @return An instance of the Barrel plugin.
     */
    public static Barrel getBarrel() {
        return barrel;
    }

    /**
     * Fetches a set of all of the crates defined in the configuration.
     *
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

            ArrayList<Reward> rewards = new ArrayList<>();
            crateConfig.getConfigurationSection("rewards").getKeys(false).stream().forEach(rewardId -> {
                ConfigurationSection rewardConfig = crateConfig.getConfigurationSection("rewards." + rewardId);
                Material rewardMaterial = Material.getMaterial(rewardConfig.getString("item.material", "BARRIER"));

                ItemStack rewardItem = new ItemBuilder(
                        new ItemStack(
                                rewardMaterial == null ? Material.BARRIER : rewardMaterial,
                                rewardConfig.getInt("item.amount", 1)
                        ))
                        .name(rewardConfig.getString("item.name", ""))
                        .lore(rewardConfig.getStringList("item.lore"))
                        .build();

                rewards.add(new Reward(
                        rewardItem,
                        rewardConfig.getDouble("chance", 0),
                        rewardConfig.getBoolean("enchanted"),
                        rewardConfig.getStringList("commands"),
                        rewardConfig.getStringList("messages")
                ));
            });

            ConfigurationSection locationConfig = crateConfig.getConfigurationSection("location");
            Block crateBlock = Bukkit.getWorld(locationConfig.getString("world")).getBlockAt(
                    locationConfig.getInt("x"),
                    locationConfig.getInt("y"),
                    locationConfig.getInt("z")
            );

            getCrates().add(new Crate(
                    crateName,
                    crateConfig.getString("color"),
                    crateBlock,
                    rewards,
                    crateConfig.getInt("prizes", 1)
            ));
        });
    }
}
