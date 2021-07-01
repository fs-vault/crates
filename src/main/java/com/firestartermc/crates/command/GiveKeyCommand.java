package com.firestartermc.crates.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.firestartermc.crates.Crates;
import com.firestartermc.crates.crate.Crate;
import com.firestartermc.kerosene.Kerosene;
import com.firestartermc.kerosene.command.Command;
import com.firestartermc.kerosene.util.PlayerUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@CommandAlias("givekey")
public class GiveKeyCommand extends Command {

    private final Crates crates;

    public GiveKeyCommand(@NotNull Crates crates) {
        this.crates = crates;

        var commandManager = Kerosene.getKerosene().getCommandManager();
        commandManager.registerCompletion("crates", context -> crates.registeredCrates().values().stream()
                .map(Crate::name)
                .collect(Collectors.toList()));
    }

    @Default
    @CommandCompletion("@players @crates @range:1-64")
    @CommandPermission("crates.give")
    @Syntax("<player> <crate> [amount]")
    public void onGive(CommandSender sender, OnlinePlayer player, String crateName, @Optional Integer amount) {
        var crate = crates.crateByName(crateName);

        if (crate.isEmpty()) {
            sender.sendMessage("Invalid crate type.");
            return;
        }

        if (amount == null) {
            amount = 0;
        }

        PlayerUtils.giveOrDropItem(player.getPlayer(), crate.get().key(amount));
        // TODO messages
    }
}
