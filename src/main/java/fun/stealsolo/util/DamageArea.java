package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import org.bukkit.Location;

public record DamageArea(String name, Location corner1, Location corner2, double damageMultiplier) {

    public boolean isInArea(Location location) {
        if (!corner1.getWorld().equals(corner2.getWorld())) {
            Stealsolo.getPlugin().getLogger().severe("Damage area " + name + " has different worlds!");
            return false;
        }
        return location.getX() >= Math.min(corner1.getX(), corner2.getX()) &&
               location.getX() <= Math.max(corner1.getX(), corner2.getX()) &&
               location.getY() >= Math.min(corner1.getY(), corner2.getY()) &&
               location.getY() <= Math.max(corner1.getY(), corner2.getY()) &&
               location.getZ() >= Math.min(corner1.getZ(), corner2.getZ()) &&
               location.getZ() <= Math.max(corner1.getZ(), corner2.getZ());
    }
}
