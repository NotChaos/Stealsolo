package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import org.bukkit.Location;

public record DamageArea(String name, Location corner1, Location corner2, double damageMultiplier) {

    public boolean isInArea(Location location) {
        if (location == null) {
            return false;
        }

        if (corner1 == null || corner2 == null) {
            Stealsolo.getPlugin().getLogger().severe("The " + name + " area is not properly defined!");
            return false;
        }

        if (location.getWorld() == null) {
            Stealsolo.getPlugin().getLogger().severe("The location world is null!");
            return false;
        }

        if (corner1.getWorld() == null || corner2.getWorld() == null) {
            Stealsolo.getPlugin().getLogger().severe("The " + name + " area has a corner with a null world!");
            return false;
        }

        if (!corner1.getWorld().equals(corner2.getWorld())) {
            Stealsolo.getPlugin().getLogger().severe("The " + name + " area has different worlds!");
            return false;
        }

        if (!location.getWorld().equals(corner1.getWorld())) {
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
