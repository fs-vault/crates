package xyz.nkomarn.Barrel.command;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.objects.Crate;

import java.util.*;

public class GiveKeyCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCrates: &7Give a player keys using /givekey <player> <type> [amount]."));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&6&lCrates: &7&o%s &7isn't online.", args[0]
            )));
            return true;
        }

        Optional<Crate> crate = Barrel.getCrate(args[1]);
        if (!crate.isPresent()) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&6&lCrates: &7&o%s &7isn't a valid crate.", args[1]
            )));
            return true;
        }

        int amount = 1;
        if(args.length == 3) {
            if (!NumberUtils.isNumber(args[2])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "&6&lCrates: &7&o%s &7isn't a number.", args[2]
                )));
                return true;
            }

            amount = Integer.parseInt(args[2]);
        }

        crate.get().giveKey(player, amount, false);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                "&6&lCrates: &7Gave %s %sx %s key.",
                player.getName(), amount, WordUtils.capitalize(crate.get().getName())
        )));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return null;
        } else if (args.length == 2) {
            return new ArrayList<>(Barrel.getCrateNames());
        } else if (args.length == 3) {
            return Arrays.asList("1", "2", "5", "10", "20");
        }
        return Collections.emptyList();
    }
}
