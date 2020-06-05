package xyz.nkomarn.Barrel.model;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Kerosene.gui.Gui;
import xyz.nkomarn.Kerosene.gui.GuiButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Crate {
    private final String name, displayName;
    private final Set<Reward> rewards;

    public Crate(String name, String displayName, Set<Reward> rewards) {
        this.name = name;
        this.displayName = displayName;
        this.rewards = rewards;
    }

    public String getName() {
        return this.name;
    }

    public void openPreview(Player player) {
        new Preview(player);
    }

    public void awardRandomReward(Player player) {
        List<Integer> chances = new ArrayList<>();
        // TODO
    }

    class Preview extends Gui {
        public Preview(Player player) {
            super(player, ChatColor.stripColor(displayName), 45);
            AtomicInteger slot = new AtomicInteger(-1);
            rewards.forEach(reward -> addButton(new GuiButton(
                    this,
                    reward.getItem(),
                    slot.getAndIncrement(),
                    null
            )));
            openPreview(player);
        }
    }
}
