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
            Location corner1 = Stealsolo.getMoshpitCorner1();
            Location corner2 = Stealsolo.getMoshpitCorner2();

            if (isWithinRegion(playerLocation, corner1, corner2)) {
                int multiplier = Stealsolo.getMoshpitDmgMultiplier();
                event.setDamage(event.getDamage() * multiplier);
            }
        }
    }

    private boolean isWithinRegion(Location loc, Location corner1, Location corner2) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return loc.getX() >= minX && loc.getX() <= maxX &&
                loc.getY() >= minY && loc.getY() <= maxY &&
                loc.getZ() >= minZ && loc.getZ() <= maxZ;
    }
}