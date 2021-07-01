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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@CommandAlias("keyall")
public class KeyAllCommand extends Command {

    private final Crates crates;

    public KeyAllCommand(@NotNull Crates crates) {
        this.crates = crates;
    }

    @Default
    @CommandCompletion("@crates @range:1-64")
    @CommandPermission("crates.keyall")
    @Syntax("<crate> [amount]")
    public void onKeyAll(CommandSender sender, String crateName, @Optional Integer amount) {
        var crate = crates.crateByName(crateName);

        if (crate.isEmpty()) {
            sender.sendMessage("Invalid crate type.");
            return;
        }

        if (amount == null) {
            amount = 0;
        }

        for (var player : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveOrDropItem(player, crate.get().key(amount));
        }

        var keys = amount == 1 ? "key" : "keys";
        var component = Component.text("Gave " + amount + " " + crate.get().name() + " crate " + keys + " to everyone online");
        var broadcast = Component.translatable("chat.type.admin")
                .args(getDisplayName(sender), component)
                .color(NamedTextColor.GRAY)
                .decorate(TextDecoration.ITALIC);

        Bukkit.broadcast(broadcast);
    }

    @NotNull
    private Component getDisplayName(@NotNull CommandSender sender) {
        if (sender instanceof Player player) {
            return player.displayName();
        }

        return Component.text("Server");
    }
}
