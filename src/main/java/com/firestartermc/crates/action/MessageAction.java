package com.firestartermc.crates.action;

import com.firestartermc.crates.crate.Reward;
import com.firestartermc.kerosene.util.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MessageAction(@NotNull List<String> messages) implements RewardAction {

    @Override
    public void perform(@NotNull Reward reward, @NotNull Player player) {
        for (var message : messages) {
            player.sendMessage(MessageUtils.formatColors(message, true));
        }
    }
}
