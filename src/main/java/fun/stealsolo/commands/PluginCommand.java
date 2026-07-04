package fun.stealsolo.commands;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PluginCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length == 0) {
            Message.invalid(sender, "Usage: /stealsolo <reload>");
            return false;
        }

        return switch (args[0]) {
            case "reload" -> {
                Stealsolo.reloadConfiguration();
                Message.successful(sender, "Plugin reloaded.");
                yield true;
            }
            case "setkillstreak" -> {
                if (!(sender instanceof Player player)) {
                    Message.restricted(sender, "Only players can use this command.");
                    yield false;
                }
                Stealsolo.setKillstreak(player.getUniqueId(), 10);
                Message.successful(sender, "Killstreak has been set to 10.");
                yield true;
            }
            default -> {
                Message.invalid(sender, "Usage: /stealsolo <reload>");
                yield false;
            }
        };
    }
}
