package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.BattleLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

import java.util.logging.Logger;

public class onDeathInBattleEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onDeathInBattleEvent(Plugin plugin, boolean debug) {
        onDeathInBattleEvent.plugin = plugin;
        logger = plugin.getLogger();
        onDeathInBattleEvent.debug = debug;
    }

    @EventHandler
    public void DeathInBattleEvent(PlayerDeathEvent e) {
        BattleLocation found = null;
        Player winner = null;
        Player dead = e.getPlayer();

        for (BattleLocation location : Stealsolo.getBattleLocations()) {
            Player first = location.firstPlayer();
            Player second = location.secondPlayer();

            if (first != null && first.getUniqueId().equals(dead.getUniqueId())) {
                found = location;
                winner = second;
                break;
            }

            if (second != null && second.getUniqueId().equals(dead.getUniqueId())) {
                found = location;
                winner = first;
                break;
            }
        }

        if (found == null) {
            return;
        }

        if (winner != null) {
            Message.successful(winner, Message.replaceInComponent(DuckAPI.getLanguageComponent("1v1.End.Win"), "%opponent%", e.getPlayer().getName()));
            Stealsolo.getDeluxecombatApi().untag(winner);
        }

        dead.teleport(Stealsolo.getSpawnLocation());

        BattleLocation arena = found;

        Bukkit.getScheduler().runTaskLater(Stealsolo.getPlugin(), () -> {
            if (arena.firstPlayer() != null) {
                arena.firstPlayer().teleport(Stealsolo.getSpawnLocation());
            }
            if (arena.secondPlayer() != null) {
                arena.secondPlayer().teleport(Stealsolo.getSpawnLocation());
            }

            Stealsolo.getBattleLocations().remove(arena);
            Stealsolo.getBattleLocations().add(new BattleLocation(arena.spawnPoint(), false, null, null));
        }, 20L * 10);
    }
}
