package com.firestartermc.crates;

import com.firestartermc.crates.command.GiveKeyCommand;
import com.firestartermc.crates.crate.Crate;
import com.firestartermc.crates.crate.CrateLoader;
import com.firestartermc.crates.listener.InteractionListener;
import com.firestartermc.crates.listener.KeyPlaceListener;
import com.firestartermc.kerosene.Kerosene;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Crates extends JavaPlugin {

    private Map<Block, Crate> registeredCrates;

    @Override
    public void onEnable() {
        registeredCrates = CrateLoader.loadAllCrates(this);

        var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new InteractionListener(this), this);
        pluginManager.registerEvents(new KeyPlaceListener(), this);

        var commandManager = Kerosene.getKerosene().getCommandManager();
        commandManager.registerCommands(new GiveKeyCommand(this));
    }

    @NotNull
    public Map<Block, Crate> registeredCrates() {
        return registeredCrates;
    }
}
