package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Message {

    public static void invalid(Player p, String message) {
        p.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    public static void invalid(CommandSender sender, String message) {
        if (sender instanceof Player player) {
            invalid(player, message);
            return;
        }

        sender.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
    }

    public static void invalid(Player player, TextComponent message) {
        player.spigot().sendMessage(message);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    public static void successful(Player player, String message) {
        player.sendMessage(Stealsolo.getPrefix() + ChatColor.GREEN + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void successful(CommandSender sender, String message) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Stealsolo.getPrefix() + ChatColor.GREEN + message);
            return;
        }

        successful(player, message);
    }

    public static void successful(Player player, TextComponent message) {
        player.spigot().sendMessage(message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void restricted(Player player, String message) {
        player.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    public static void restricted(CommandSender sender, String message) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
            return;
        }

        restricted(player, message);
    }

    public static void restricted(Player player, TextComponent message) {
        player.spigot().sendMessage(message);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }


    public static void important(Player player, String message) {
        player.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }

    public static void important(CommandSender sender, String message) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Stealsolo.getPrefix() + ChatColor.RED + message);
            return;
        }

        important(player, message);
    }

    public static void important(Player player, TextComponent message) {
        player.spigot().sendMessage(message);
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }
}
