package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCoinsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player p)) {
            Message.invalid(sender, "Only players can use this command.");
            return false;
        }

        if (args.length != 2) {
            Message.invalid(sender, "Usage: /paycoins <player> <amount>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            Message.invalid(sender, "Player " + ChatColor.BOLD + args[0] + ChatColor.RESET + ChatColor.RED + " not found.");
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Message.invalid(sender, "Amount must be a number.");
            return false;
        }

        if (amount < 1) {
            Message.invalid(sender, "Amount must be greater than 0.");
            return false;
        }

        Stealsolo.getPpAPI().pay(p.getUniqueId(), target.getUniqueId(), amount);
        MiniMessage miniMessage = MiniMessage.miniMessage();

        Message.successful(sender, miniMessage.deserialize(
                Message.convertToString(
                        Stealsolo.getPaycoinsMessageSender()
                                .replace("%player%", p.getName())
                                .replace("%amount%", String.valueOf(amount))
                                .replace("%target%", target.getName())
                )));
        Message.successful(target, miniMessage.deserialize(
                Message.convertToString(
                        Stealsolo.getPaycoinsMessageRecipient()
                                .replace("%player%", p.getName())
                                .replace("%amount%", String.valueOf(amount))
                                .replace("%target%", target.getName())
                )));
        return false;
    }
}
