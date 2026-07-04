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

import java.util.UUID;

public class AcceptBattleCommand implements CommandExecutor {

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

        if (target.getName().equalsIgnoreCase(player.getName())) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.SelfRequest"));
            return false;
        }

        UUID uuid = Stealsolo.getBattleRequests().get(player.getUniqueId());

        if (uuid == null) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.NoRequest"));
            return false;
        }

        Player requester = Bukkit.getPlayer(uuid);

        if (requester == null) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.NoRequest"));
            return false;
        }

        if (Stealsolo.getDeluxecombatApi().isInCombat(player)) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.InCombat.Self"));
            Stealsolo.getBattleRequests().remove(player.getUniqueId(), target.getUniqueId());
            Stealsolo.getBattleRequests().remove(target.getUniqueId(), player.getUniqueId());
            return false;
        }

        if (Stealsolo.getDeluxecombatApi().isInCombat(target)) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.InCombat.Target"));
            Stealsolo.getBattleRequests().remove(player.getUniqueId(), target.getUniqueId());
            Stealsolo.getBattleRequests().remove(target.getUniqueId(), player.getUniqueId());
            return false;
        }

        BattleLocation freeLocation = null;
        for (BattleLocation location : Stealsolo.getBattleLocations()) {
            if (!location.active()) {
                freeLocation = location;
                break;
            }
        }

        if (freeLocation == null) {
            Message.invalid(player, DuckAPI.getLanguageComponent("1v1.NoFreeArena"));
            return false;
        }

        Stealsolo.getBattleRequests().remove(target.getUniqueId());
        Stealsolo.getBattleLocations().remove(freeLocation);

        BattleLocation active = new BattleLocation(freeLocation.spawnPoint(), true, player, target);
        Stealsolo.getBattleLocations().add(active);

        target.teleport(active.spawnPoint());
        player.teleport(active.spawnPoint());

        Stealsolo.getDeluxecombatApi().tag(player, target, 60 * 10);

        Message.successful(player, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.Request.Accepted"), "%player%", target.getName()));
        Message.successful(target, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.Request.Accepted"), "%player%", player.getName()));

        Stealsolo.getDeluxecombatApi().tag(player, target, 1200);

        Bukkit.getScheduler().runTaskLater(Stealsolo.getPlugin(), () -> {
            if (player.isOnline() && target.isOnline() &&
                    ((active.firstPlayer().equals(player) && active.secondPlayer().equals(target)) ||
                            (active.firstPlayer().equals(target) && active.secondPlayer().equals(player)))) {

                Stealsolo.getDeluxecombatApi().untag(player);
                Stealsolo.getDeluxecombatApi().untag(target);
                player.teleport(Stealsolo.getSpawnLocation());
                target.teleport(Stealsolo.getSpawnLocation());

                Message.restricted(player, DuckAPI.getLanguageComponent("1v1.End.TooLong"));
                Message.restricted(target, DuckAPI.getLanguageComponent("1v1.End.TooLong"));

                Stealsolo.getBattleLocations().remove(active);
                Stealsolo.getBattleLocations().add(new BattleLocation(active.spawnPoint(), false, null, null));
            }
        }, 20L * 60 * 10);

        return false;
    }

}
