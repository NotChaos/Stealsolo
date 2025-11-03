package fun.stealsolo.events;

import fun.stealsolo.Stealsolo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

import java.util.logging.Logger;

public class onPlayerDeathEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onPlayerDeathEvent(Plugin plugin, boolean debug) {
        onPlayerDeathEvent.plugin = plugin;
        logger = plugin.getLogger();
        onPlayerDeathEvent.debug = debug;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getDeathPenaltyCommand().replace("%player%", e.getPlayer().getName()));

        if (!(e.getDamageSource().getCausingEntity() instanceof Player killer)) {
            return;
        }

        if (killer == e.getPlayer()) {
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getKillRewardCommand().replace("%player%", killer.getName()));
    }
}
