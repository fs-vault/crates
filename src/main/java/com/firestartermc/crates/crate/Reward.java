package com.firestartermc.crates.crate;

import com.firestartermc.crates.action.RewardAction;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a winnable prize from a {@link Crate}.
 *
 * @since 1.0
 */
public record Reward(@NotNull ItemStack item, double chance, @NotNull List<RewardAction> actions) {
}
