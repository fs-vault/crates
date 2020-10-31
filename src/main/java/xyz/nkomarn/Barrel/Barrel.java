package xyz.nkomarn.Barrel;

import com.firestartermc.kerosene.item.ItemBuilder;
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
import java.util.*;

public class Barrel extends JavaPlugin {

    public static NamespacedKey CRATE_NAMESPACE;
    private static Barrel barrel;

    private static final Map<String, Crate> nameCrateLookup = new HashMap<>();
    private static final Map<Block, Crate> blockCrateLookup = new HashMap<>();

    public void onEnable() {
        CRATE_NAMESPACE = new NamespacedKey(this, "crate");
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
        return new HashSet<>(nameCrateLookup.values());
    }

    /**
     * Gets a set of all names of the crates defined in the configuration.
     *
     * @return A set of all names of the creates loaded from the configuration.
     */
    public static Set<String> getCrateNames() {
        return nameCrateLookup.keySet();
    }

    /**
     * Gets a crate that was loaded from configuration based on the name.
     *
     * @param name of the crate
     * @return optional crate
     */
    public static Optional<Crate> getCrate(String name) {
        return Optional.ofNullable(nameCrateLookup.get(name));
    }

    /**
     * Gets a create that was loaded from configuration based on block.
     *
     * @param block of the crate
     * @return optional crate
     */
    public static Optional<Crate> getCrate(Block block) {
        return Optional.ofNullable(blockCrateLookup.get(block));
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

                ItemStack rewardItem = ItemBuilder.of(rewardMaterial == null ? Material.BARRIER : rewardMaterial)
                        .amount(rewardConfig.getInt("item.amount", 1))
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

            Crate crate = new Crate(
                    crateName,
                    crateConfig.getString("color"),
                    crateBlock,
                    rewards,
                    crateConfig.getInt("prizes", 1)
            );

            nameCrateLookup.put(crateName, crate);
            blockCrateLookup.put(crateBlock, crate);
        });
    }
}
