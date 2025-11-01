package fun.stealsolo.commands;

import fun.stealsolo.events.onInventoryCloseEvent;
import com.duckydeveloper.util.Message;
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
        if (!(sender instanceof Player player)) {
            Message.restricted(sender);
            return false;
        }

        if (args.length != 0) {
            Message.invalid(sender, "Usage: /trash");
            return false;
        }

        Inventory inventory = Bukkit.createInventory(player, 54, "TrashGUI");

        onInventoryCloseEvent.getPlayers().add(player);

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_BARREL_OPEN, 1.0F, 1.0F);
        return true;
    }
}
