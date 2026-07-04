package fun.stealsolo.events;

import fun.stealsolo.util.LimitCombatItemsManager;
import lombok.Getter;
import nl.marido.deluxecombat.events.CombatStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class onCombatStateChangeEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onCombatStateChangeEvent(Plugin plugin, boolean debug) {
        onCombatStateChangeEvent.plugin = plugin;
        logger = plugin.getLogger();
        onCombatStateChangeEvent.debug = debug;
    }

    @EventHandler
    public void CombatStateChangeEvent(CombatStateChangeEvent e) {
        if (!e.getState().equals(CombatStateChangeEvent.CombatState.TAGGED)) {
            return;
        }

        if (e.getPlayer() != null && e.getPlayer() instanceof Player) {
            Player p = (Player) e.getPlayer();
            LimitCombatItemsManager.handleEnterCombat(p);
        }

    }
}
