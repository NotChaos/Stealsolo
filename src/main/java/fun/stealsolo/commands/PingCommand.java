package fun.stealsolo.commands;

import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player p)) {
            Message.invalid(sender, "You must be a player to execute this command.");
            return false;
        }

        if (!Permission.hasPermission(sender, "ping")) {
            Message.restricted(sender);
            return false;
        }

        if (args.length != 0) {
            Message.invalid(sender, "Usage: /ping");
            return false;
        }

        Message.successful(sender,"" + ChatColor.GRAY + "Your current ping is: " + ChatColor.GREEN + p.getPing() + ChatColor.RESET + ChatColor.GREEN + "ᴍs");
        p.sendActionBar("" + ChatColor.GRAY + "Your current ping is: " + ChatColor.GREEN + p.getPing() + ChatColor.RESET + ChatColor.GREEN + "ᴍs");
        return true;
    }
}
