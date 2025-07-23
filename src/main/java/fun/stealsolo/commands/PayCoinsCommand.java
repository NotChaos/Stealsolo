package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import net.kyori.adventure.text.Component;
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
        String amountStr = args[1].toLowerCase();
        try {
            if (amountStr.endsWith("k")) {
                amount = (int) (Double.parseDouble(amountStr.substring(0, amountStr.length() - 1)) * 1_000);
            } else if (amountStr.endsWith("m")) {
                amount = (int) (Double.parseDouble(amountStr.substring(0, amountStr.length() - 1)) * 1_000_000);
            } else {
                amount = Integer.parseInt(amountStr);
            }
        } catch (NumberFormatException e) {
            Message.invalid(sender, "Amount must be a number.");
            return false;
        }

        if (amount < 1) {
            Message.invalid(sender, "Amount must be greater than 0.");
            return false;
        }

        if (Stealsolo.getPpAPI().look(p.getUniqueId()) < amount) {
            Message.invalid(p, "You do not have enough coins to transfer " + amount);
            return false;
        }

        Stealsolo.getPpAPI().pay(p.getUniqueId(), target.getUniqueId(), amount);

        Component senderComponent = Message.convertStringToComponent(
                Stealsolo.getPaycoinsMessageSender()
                        .replace("%player%", p.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%target%", target.getName())
        );

        Message.successful(sender, senderComponent);
        sender.sendActionBar(senderComponent);

        Component targetComponent = Message.convertStringToComponent(
                Stealsolo.getPaycoinsMessageRecipient()
                        .replace("%player%", p.getName())
                        .replace("%amount%", String.valueOf(amount))
                        .replace("%target%", target.getName())
        );

        Message.successful(target, targetComponent);
        target.sendActionBar(targetComponent);
        return false;
    }
}
