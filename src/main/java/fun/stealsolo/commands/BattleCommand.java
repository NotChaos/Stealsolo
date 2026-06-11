package fun.stealsolo.commands;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.BattleLocation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BattleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.invalid(sender, "Only players can execute this command.");
            return false;
        }

        if (Stealsolo.getBattleLocations().size() == 0) {
            Message.restricted(player, DuckAPI.getLanguageComponent("1v1.NotSetup"));
            return false;
        }

        if (args.length == 0) {
            Message.invalid(sender, "Usage: /1v1 <target>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Message.invalid(sender, "Player not found.");
            return false;
        }

        boolean available = false;

        for (BattleLocation location : Stealsolo.getBattleLocations()) {
            if (location.active()) {
                continue;
            }

            available = true;

            Message.successful(target, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.Request.Received"), "%player%", player.getName()));
            Message.successful(player, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.Request.Sent"), "%target%", target.getName()));
            Stealsolo.getBattleRequests().put(target.getUniqueId(), player.getUniqueId());
            break;
        }

        if (!available) {
            Message.restricted(player, DuckAPI.getLanguageComponent("1v1.NoFreeArena"));
        }




        return true;
    }

}
