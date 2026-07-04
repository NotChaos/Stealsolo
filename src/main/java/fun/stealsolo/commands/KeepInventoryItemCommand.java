package fun.stealsolo.commands;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KeepInventoryItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length == 0) {
            Message.invalid(sender, "Usage: /keepinventoryitem <give|set|check>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "give" -> {
                if (!(sender instanceof Player player)) {
                    Message.restricted(sender, "Only players can use this command.");
                    return false;
                }

                player.getInventory().addItem(Stealsolo.getKeepInventoryItem());
                Message.successful(sender, "You have received the keep inventory item.");
                return true;
            }
            case "set" -> {
                if (args.length != 3) {
                    Message.invalid(sender, "Usage: /keepinventoryitem set <player> <amount>");
                    return false;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                if (player == null) {
                    Message.invalid(sender, "Player " + args[1] + " not found.");
                    return false;
                }
                int amount;

                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    Message.invalid(sender, "Amount must be a number.");
                    return false;
                }

                if (amount <= 0) {
                    Message.invalid(sender, "Amount must be a positive number.");
                    return false;
                }

                Stealsolo.setKeepinventoryBalance(player.getUniqueId(), amount);
                Message.successful(sender, "Set " + player.getName() + "'s keep inventory item balance to " + amount + ".");
                return true;
            }
            case "check" -> {
                if (args.length != 2) {
                    Message.invalid(sender, "Usage: /keepinventoryitem check <player>");
                    return false;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);

                Message.successful(sender, "Player " + player.getName() + " has " + Stealsolo.getKeepinventoryBalance(player.getUniqueId()) + " keep inventory items.");
                return true;
            }
            default -> {
                Message.invalid(sender, "Usage: /keepinventoryitem <give|set|check>");
                return false;
            }
        }
    }
}