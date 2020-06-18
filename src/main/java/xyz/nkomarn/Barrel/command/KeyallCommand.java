package xyz.nkomarn.Barrel.command;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.objects.Crate;

import java.util.*;

public class KeyallCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCrates: &7Give everyone a key using /keyall <type> [amount]."));
        } else {
            Optional<Crate> crate = Barrel.getCrate(args[0]);
            if (!crate.isPresent()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "&6&lCrates: &7&o%s &7isn't a valid crate.", args[0]
                )));
                return true;
            }

            int amount = 1;
            if (args.length == 2) {
                if (!NumberUtils.isNumber(args[1])) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                            "&6&lCrates: &7&o%s &7isn't a number.", args[1]
                    )));
                    return true;
                }
                amount = Integer.parseInt(args[1]);
            }

            int finalAmount = amount;
            Bukkit.getOnlinePlayers().forEach(player -> crate.get().giveKey(player, finalAmount, true));

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&6&lCrates: &7Gave %sx %s key to everyone online.",
                    amount, WordUtils.capitalize(crate.get().getName())
            )));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Barrel.getCrateNames());
        } else if (args.length == 2) {
            return Arrays.asList("1", "2", "5", "10", "20");
        }
        return Collections.emptyList();
    }
}
