package xyz.nkomarn.Barrel.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.nkomarn.Barrel.Barrel;
import xyz.nkomarn.Barrel.model.Crate;

public class GiveKeyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6&lCrates: &7Give a player keys using /givekey [player] [type] [amount]."));
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            String keyType = args[1];
            int amount = Integer.parseInt(args[2]);

            for (Crate crate : Barrel.crates) {
                if (crate.getName().equalsIgnoreCase(keyType)) {
                    crate.giveKey(player, amount);
                    return true;
                }
            }
        }
        return true;
    }
}
