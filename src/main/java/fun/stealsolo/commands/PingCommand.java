package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import org.bukkit.Bukkit;
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

        if (args.length > 1) {
            Message.invalid(sender, "Usage: /ping");
            return false;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Message.invalid(sender, "Player " + ChatColor.BOLD + args[0] + ChatColor.RESET + ChatColor.RED + " not found.");
                return false;
            }

            Message.successful(sender, Message.convertStringToComponent(Stealsolo.getPingMessageOthers()
                    .replace("%ping%", String.valueOf(p.getPing()))
                    .replace("%target%", target.getName())
                    ));
            return false;
        }

        Message.successful(sender, Message.convertStringToComponent(Stealsolo.getPingMessageSelf()
                .replace("%ping%", String.valueOf(p.getPing())
                )));
        p.sendActionBar(Message.convertStringToComponent(Stealsolo.getPingMessageSelf()
                .replace("%ping%", String.valueOf(p.getPing())
                )));
        return true;
    }
}
