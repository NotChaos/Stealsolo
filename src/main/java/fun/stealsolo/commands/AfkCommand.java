package fun.stealsolo.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class AfkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player)) {
            Message.restricted(sender);
            return false;
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");

        if (!(plugin instanceof MultiverseCore multiverseCore)) {
            Message.invalid(sender, "This command requires Multiverse-Core to be installed.");
            return false;
        }

        multiverseCore.teleportPlayer(sender, (Player) sender, Stealsolo.getAfkLocation());
        return false;
    }
}
