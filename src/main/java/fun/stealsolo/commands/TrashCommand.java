package fun.stealsolo.commands;

import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class TrashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length != 0) {
            Message.invalid(sender, "Usage: /trash");
            return false;
        }

        if (!(sender instanceof Player player)) {
            Message.restricted(sender);
            return false;
        }

        if (!Permission.hasPermission(player, "trash")) {
            Message.restricted(player);
            return false;
        }

        Inventory inventory = Bukkit.createInventory(player, 54, "TrashGUI");

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0F, 1.0F);
        return true;
    }
}
