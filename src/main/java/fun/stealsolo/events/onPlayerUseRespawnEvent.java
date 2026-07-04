package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import fun.stealsolo.Stealsolo;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.logging.Logger;

public class onPlayerUseRespawnEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onPlayerUseRespawnEvent(Plugin plugin, boolean debug) {
        onPlayerUseRespawnEvent.plugin = plugin;
        logger = plugin.getLogger();
        onPlayerUseRespawnEvent.debug = debug;
    }

    @EventHandler
    public void PlayerUseRespawnEvent(PlayerDeathEvent e) {
        Player player = e.getPlayer();

        if (Stealsolo.isReviveEnabled()) {
            if (Stealsolo.getKeepinventoryBalance(player.getUniqueId()) > 0) {
                e.setCancelled(true);
                Stealsolo.setKeepinventoryBalance(player.getUniqueId(), Stealsolo.getKeepinventoryBalance(player.getUniqueId()) - 1);
                player.getWorld().playSound(player.getLocation(), Stealsolo.getReviveSound(), 1.0F, 1.0F);
                Stealsolo.getReviveParticles().forEach(particle -> {
                    Particle.valueOf(particle.particle().name()).builder()
                            .location(player.getLocation())
                            .offset(particle.offsetX(), particle.offsetY(), particle.offsetZ())
                            .count(particle.count())
                            .receivers(32, true)
                            .extra(particle.speed())
                            .spawn();
                    // player.getWorld().spawnParticle(particle.particle(), player.getLocation(), particle.count(), particle.speed());
                });
                player.setHealth(20 * Stealsolo.getReviveHealPercent() / 100);
            }
        } else {
            Arrays.stream(player.getInventory().getContents().clone()).toList().forEach(itemStack -> {
                if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getItemMeta() == null) {
                    return;
                }

                if (Boolean.TRUE.equals(itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Stealsolo.getPlugin(), DuckAPI.getPersistentDataKey() + ".keepInventory"), PersistentDataType.BOOLEAN))) {
                    itemStack.setAmount(itemStack.getAmount() - 1);

                    e.setKeepInventory(true);
                    e.getDrops().clear();
                }
            });
        }
    }
}
