package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    public static void invalid(Player p, String message) {
        invalid(p, message, true);
    }

    public static void invalid(Player p, String message, boolean withPrefix) {
        p.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + message);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    public static void invalid(CommandSender sender, String message) {
        invalid(sender, message, true);
    }

    public static void invalid(CommandSender sender, String message, boolean withPrefix) {
        if (sender instanceof Player player) {
            invalid(player, message, withPrefix);
            return;
        }

        sender.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + message);
    }

    public static void invalid(Player player, TextComponent message) {
        invalid(player, message, true);
    }

    public static void invalid(Player player, TextComponent message, boolean withPrefix) {
        if (withPrefix) {
            player.spigot().sendMessage(new TextComponent(Stealsolo.getPrefix() + message.getText()));
        } else {
            player.spigot().sendMessage(message);
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    public static void successful(Player player, String message) {
        successful(player, message, true);
    }

    public static void successful(Player player, String message, boolean withPrefix) {
        player.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.GREEN + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void successful(CommandSender sender, String message) {
        successful(sender, message, true);
    }

    public static void successful(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.GREEN + message);
            return;
        }

        successful(player, message, withPrefix);
    }

    public static void successful(Player player, TextComponent message) {
        successful(player, message, true);
    }

    public static void successful(Player player, TextComponent message, boolean withPrefix) {
        if (withPrefix) {
            player.spigot().sendMessage(new TextComponent(Stealsolo.getPrefix() + message.getText()));
        } else {
            player.spigot().sendMessage(message);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void restricted(Player player, String message) {
        restricted(player, message, true);
    }

    public static void restricted(Player player, String message, boolean withPrefix) {
        player.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + message);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    public static void restricted(CommandSender sender, String message) {
        restricted(sender, message, true);
    }

    public static void restricted(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + Stealsolo.getInsufficentPermissions());
            return;
        }

        restricted(player, message, withPrefix);
    }

    public static void restricted(Player player) {
        restricted(player, Stealsolo.getInsufficentPermissions(), true);
    }

    public static void restricted(CommandSender sender) {
        restricted(sender, Stealsolo.getInsufficentPermissions(), true);
    }

    public static void restricted(Player player, TextComponent message) {
        restricted(player, message, true);
    }

    public static void restricted(Player player, TextComponent message, boolean withPrefix) {
        if (withPrefix) {
            player.spigot().sendMessage(new TextComponent(Stealsolo.getPrefix() + message.getText()));
        } else {
            player.spigot().sendMessage(message);
        }
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    public static void important(Player player, String message) {
        important(player, message, true);
    }

    public static void important(Player player, String message, boolean withPrefix) {
        player.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + message);
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }

    public static void important(CommandSender sender, String message) {
        important(sender, message, true);
    }

    public static void important(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage((withPrefix ? Stealsolo.getPrefix() : "") + ChatColor.RED + message);
            return;
        }

        important(player, message, withPrefix);
    }

    public static void important(Player player, TextComponent message) {
        important(player, message, true);
    }

    public static void important(Player player, TextComponent message, boolean withPrefix) {
        if (withPrefix) {
            player.spigot().sendMessage(new TextComponent(Stealsolo.getPrefix() + message.getText()));
        } else {
            player.spigot().sendMessage(message);
        }
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }
}