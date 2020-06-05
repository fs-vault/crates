package xyz.nkomarn.Barrel;

import com.google.common.io.Files;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import sun.security.krb5.Config;
import xyz.nkomarn.Barrel.listener.BlockInteractListener;
import xyz.nkomarn.Barrel.model.Crate;
import xyz.nkomarn.Barrel.model.Reward;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Barrel extends JavaPlugin {
    private static Barrel barrel;
    public static final List<Crate> crates = new ArrayList<>();

    public void onEnable() {
        barrel = this;
        saveDefaultConfig();
        loadCrates();

        getServer().getPluginManager().registerEvents(new BlockInteractListener(), this);
    }

    public static Barrel getBarrel() {
        return barrel;
    }

    private void loadCrates() {
        ConfigurationSection crates = getConfig().getConfigurationSection("crates");
        for (String crateName : crates.getKeys(false)) {
            ConfigurationSection crateSection = crates.getConfigurationSection(crateName);
            Set<Reward> rewards = new HashSet<>();

            for (String rewardId : crateSection.getConfigurationSection("rewards").getKeys(false)) {
                ConfigurationSection rewardSection = crateSection.getConfigurationSection("rewards." + rewardId);
                rewards.add(new Reward(
                        rewardSection.getItemStack("item"),
                        rewardSection.getDouble("chance"),
                        rewardSection.getBoolean("enchanted"),
                        rewardSection.getStringList("commands").toArray(new String[0]),
                        rewardSection.getStringList("messages").toArray(new String[0]),
                        rewardSection.getBoolean("awardItem")
                ));
            }

            Barrel.crates.add(new Crate(crateName, crateSection.getString("displayname"), rewards));
        }
    }
}
