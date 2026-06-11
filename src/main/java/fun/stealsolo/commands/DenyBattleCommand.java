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

public class DenyBattleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.invalid(sender, "Only players can execute this command.");
            return false;
        }

        if (args.length == 0) {
            Message.invalid(sender, "Usage: /accept1v1 <playername>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Message.invalid(sender, "Player not found.");
            return false;
        }

        Player requester = Bukkit.getPlayer(Stealsolo.getBattleRequests().get(player.getUniqueId()));

        if (requester == null) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.NoRequest"));
            return false;
        }

        Stealsolo.getBattleRequests().remove(player.getUniqueId(), target.getUniqueId());
        Stealsolo.getBattleRequests().remove(target.getUniqueId(), player.getUniqueId());
        Message.successful(player, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.Request.Denied"), "%target%", target.getName()));
        return true;
    }

}
