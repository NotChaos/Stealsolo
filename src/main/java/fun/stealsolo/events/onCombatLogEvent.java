package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.BattleLocation;
import nl.marido.deluxecombat.events.CombatlogEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

import javax.swing.plaf.basic.BasicButtonUI;
import java.util.logging.Logger;

public class onCombatLogEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onCombatLogEvent(Plugin plugin, boolean debug) {
        onCombatLogEvent.plugin = plugin;
        logger = plugin.getLogger();
        onCombatLogEvent.debug = debug;
    }

    @EventHandler
    public void CombatLogEvent(CombatlogEvent e) {
        Stealsolo.getBattleLocations().forEach(location -> {
            if (location.firstPlayer().equals(e.getCombatlogger().getPlayer().getUniqueId())) {
                Message.successful(location.secondPlayer(), DuckAPI.getLanguageComponent("1v1.End.OpponentCombatLogged"));
                e.getCombatlogger().teleport(Stealsolo.getSpawnLocation());
                Stealsolo.getDeluxecombatApi().untag(location.secondPlayer());

                Bukkit.getScheduler().runTaskLater(Stealsolo.getPlugin(), () -> {
                    if (location.secondPlayer() != null) {
                        location.secondPlayer().teleport(Stealsolo.getSpawnLocation());
                    }

                    Stealsolo.getBattleLocations().remove(location);
                    Stealsolo.getBattleLocations().add(
                            new BattleLocation(
                                    location.spawnPoint(),
                                    false,
                                    null,
                                    null
                            )
                    );
                }, 20L * 10);
            }

            if (location.secondPlayer().equals(e.getCombatlogger().getPlayer().getUniqueId())) {
                Message.successful(location.firstPlayer(), DuckAPI.getLanguageComponent("1v1.End.OpponentCombatLogged"));
                e.getCombatlogger().teleport(Stealsolo.getSpawnLocation());
                Stealsolo.getDeluxecombatApi().untag(location.firstPlayer());

                Bukkit.getScheduler().runTaskLater(Stealsolo.getPlugin(), () -> {
                    if (location.firstPlayer() != null) {
                        location.firstPlayer().teleport(Stealsolo.getSpawnLocation());
                    }

                    Stealsolo.getBattleLocations().remove(location);
                    Stealsolo.getBattleLocations().add(
                            new BattleLocation(
                                    location.spawnPoint(),
                                    false,
                                    null,
                                    null
                            )
                    );
                }, 20L * 10);
            }
        });
    }
}

