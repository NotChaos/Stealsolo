package fun.stealsolo.events;

import fun.stealsolo.Stealsolo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class onItemPickupEvent implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (Stealsolo.getAntiPickupList().contains(player.getUniqueId().toString())) {
                e.setCancelled(true);
            }
        }
    }
}
