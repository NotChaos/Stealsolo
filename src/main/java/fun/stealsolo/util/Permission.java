package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permission {

    public static boolean hasPermission(Player player, String permission) {
        if (Stealsolo.isDebug()) {
            Stealsolo.getPlugin().getLogger().info("Checking permission " + permission + " for player " + player.getName());
        }

        return player.hasPermission(Stealsolo.getPermissionPrefix() + "." + permission);
    }

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof Player player) {
            return hasPermission(player, permission);
        }
        return true;
    }
}
