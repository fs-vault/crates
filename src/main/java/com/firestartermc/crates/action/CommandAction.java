package com.firestartermc.crates.action;

import com.firestartermc.crates.crate.Reward;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CommandAction(@NotNull List<String> commands) implements RewardAction {

    @Override
    public void perform(@NotNull Reward reward, @NotNull Player player) {
        var console = Bukkit.getConsoleSender();

        for (var command : commands()) {
            Bukkit.dispatchCommand(console, command.replace("{player}", player.getName()));
        }
    }
}
