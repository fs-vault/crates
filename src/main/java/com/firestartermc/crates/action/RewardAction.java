package com.firestartermc.crates.action;

import com.firestartermc.crates.crate.Reward;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RewardAction {

    void perform(@NotNull Reward reward, @NotNull Player player);
}
