package fun.stealsolo.events;

import fun.stealsolo.Stealsolo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class onDamageEvent implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            Location playerLocation = player.getLocation();

            Stealsolo.getDamageAreas().forEach(area -> {
                if (area.isInArea(playerLocation)) {
                    event.setDamage(event.getDamage() * area.damageMultiplier());
                }
            });
        }
    }
}