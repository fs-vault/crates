package xyz.nkomarn.Barrel.command;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.objects.Crate;

import java.util.Optional;

public class KeyallCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCrates: &7Give everyone a key using /keyall [type] [amount]."));
        } else {
            Optional<Crate> crate = Barrel.getCrates().stream()
                    .filter(c -> c.getName().equalsIgnoreCase(args[0]))
                    .findFirst();

            if (crate.isEmpty()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "&6&lCrates: &7&o%s &7isn't a valid crate.", args[0]
                )));
                return true;
            }

            if (!NumberUtils.isNumber(args[1])) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                        "&6&lCrates: &7&o%s &7isn't a number.", args[1]
                )));
                return true;
            }

            int amount = Integer.parseInt(args[1]);
            Bukkit.getOnlinePlayers().forEach(player -> crate.get().giveKey(player, amount, true));

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format(
                    "&6&lCrates: &7Gave %sx %s key to everyone online.",
                    amount, WordUtils.capitalize(crate.get().getName())
            )));
        }
        return true;
    }
}
