package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.KillEntry;
import org.bukkit.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

public class onPlayerDeathEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;
    private static Configuration config;
    private static final HashSet<KillEntry> killEntries = new HashSet<>();

    public onPlayerDeathEvent(Plugin plugin, boolean debug) {
        onPlayerDeathEvent.plugin = plugin;
        config = plugin.getConfig();
        logger = plugin.getLogger();
        onPlayerDeathEvent.debug = debug;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        int victimKillstreak = Stealsolo.getKillstreak(player.getUniqueId());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Stealsolo.getDeathPenaltyCommand().replace("%player%", player.getName()));
        Stealsolo.setKillstreak(player.getUniqueId(), 0);

        if (debug) {
            logger.info("[DEBUG] Executing death penalty; KillStreakLostMessage " + config.getString("DeathPenalty.KillStreakLostMessage", "NULL AT DeathPenalty.KillStreakLostMessage"));
            logger.info("[DEBUG] KillStreakThreshold " + config.getInt("DeathPenalty.KillStreakThreshold", 0));
            logger.info("[DEBUG] KillStreakMessage " + config.getString("DeathPenalty.KillStreakMessage", "NULL AT DeathPenalty.KillStreakMessage"));
            logger.info("[DEBUG] Previous killstreak " + victimKillstreak);
        }

        if (victimKillstreak >= config.getInt("DeathPenalty.KillStreakThreshold", 5)) {
            Bukkit.broadcast(Message.convertStringToComponent(config.getString("DeathPenalty.KillStreakLostMessage", "&5%player% has lost his killstreak of %killstreak%!")
                    .replace("%player%", player.getName())
                    .replace("%killstreak%", String.valueOf(victimKillstreak))
            ));
        }

        Player killer = e.getEntity().getKiller();
        if (killer == null) {
            return;
        }

        if (killer == player) {
            return;
        }

        int killstreak = Stealsolo.getKillstreak(killer.getUniqueId()) + 1;

        Stealsolo.setKillstreak(killer.getUniqueId(), killstreak);

        if (killstreak % config.getInt("DeathPenalty.KillStreakThreshold", 5) == 0) {
            Bukkit.broadcast(Message.convertStringToComponent(config.getString("DeathPenalty.KillStreakMessage", "&5%player% is on a killstreak of %killstreak% kills!")
                    .replace("%player%", killer.getName())
                    .replace("%killstreak%", String.valueOf(killstreak))
            ));
        }

        KillEntry lastKillEntry = null;
        for (KillEntry entry : killEntries) {
            if (entry.killedUUID() == player.getUniqueId() && entry.killerUUID() == killer.getUniqueId()) {
                lastKillEntry = entry;
                break;
            }
        }

        Timestamp lastRewardTime = null;

        if (lastKillEntry == null) {
            lastRewardTime = new Timestamp(0);
        } else {
            lastRewardTime = lastKillEntry.timestamp();
        }

        if (lastRewardTime.getTime() > System.currentTimeMillis()) {
            return;
        }

        KillEntry killEntry = new KillEntry(killer.getUniqueId(), player.getUniqueId(), new Timestamp(System.currentTimeMillis() + (1000L * 60L)));

        killEntries.add(killEntry);

        Bukkit.getScheduler().runTaskLater(Stealsolo.getPlugin(), () -> killEntries.remove(killEntry), 60 * 20L);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), config.getString("DeathPenalty.RewardCommand", "eco give %player% 5").replace("%player%", killer.getName()));
    }
}
