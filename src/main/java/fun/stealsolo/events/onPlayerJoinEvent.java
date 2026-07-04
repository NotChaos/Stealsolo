package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class onPlayerJoinEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onPlayerJoinEvent(Plugin plugin, boolean debug) {
        onPlayerJoinEvent.plugin = plugin;
        logger = plugin.getLogger();
        onPlayerJoinEvent.debug = debug;
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerTeleportEvent e) {
        Player player = e.getPlayer();

        if (player.getFirstPlayed() == player.getLastPlayed()) {
            Message.successful(player, DuckAPI.getLanguageComponent("JoinMessage.FirstPlayed"));
            return;
        }
        if (PlaceholderAPI.setPlaceholders(player, "%discordsrv_user_islinked%").equals("yes")) {
            Message.successful(player, DuckAPI.getLanguageComponent("JoinMessage.LinkedPlayer"));
            return;
        }
        Message.successful(player, DuckAPI.getLanguageComponent("JoinMessage.NonLinkedPlayer"));
    }
}
