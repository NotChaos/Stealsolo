package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (Permission.hasPermission(sender, "plugin.reload")) {
            Message.restricted(sender);
            return false;
        }

        if (args.length == 0) {
            Message.invalid(sender, "Usage: /plugin <reload>");
            return false;
        }

        return switch (args[0]) {
            case "reload" -> {
                Stealsolo.reloadConfiguration();
                Message.successful(sender, "Plugin reloaded.");
                yield true;
            }
            default -> false;
        };
    }
}
