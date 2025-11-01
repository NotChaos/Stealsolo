package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import com.duckydeveloper.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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
            default -> false;
        };
    }
}
