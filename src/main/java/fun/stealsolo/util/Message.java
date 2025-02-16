package fun.stealsolo.util;

import fun.stealsolo.Stealsolo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {

    public static void invalid(Player p, String message) {
        invalid(p, message, true);
    }

    public static void invalid(Player p, String message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        p.sendMessage(prefix.append(Component.text(ChatColor.RED + message)));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 1);
    }

    public static void invalid(Player p, Component message) {
        invalid(p, message, true);
    }

    public static void invalid(Player p, Component message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        p.sendMessage(prefix.append(message));
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

        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        sender.sendMessage(prefix.append(Component.text(ChatColor.RED + message)));
    }

    public static void invalid(CommandSender sender, Component message) {
        invalid(sender, message, true);
    }

    public static void invalid(CommandSender sender, Component message, boolean withPrefix) {
        if (sender instanceof Player player) {
            invalid(player, message, withPrefix);
            return;
        }

        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        sender.sendMessage(prefix.append(message));
    }

    public static void successful(Player player, String message) {
        successful(player, message, true);
    }

    public static void successful(Player player, String message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(Component.text(ChatColor.GREEN + message)));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void successful(Player player, Component message) {
        successful(player, message, true);
    }

    public static void successful(Player player, Component message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(message));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    }

    public static void successful(CommandSender sender, String message) {
        successful(sender, message, true);
    }

    public static void successful(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(Component.text(ChatColor.GREEN + message)));
            return;
        }

        successful((Player) sender, message, withPrefix);
    }

    public static void successful(CommandSender sender, Component message) {
        successful(sender, message, true);
    }

    public static void successful(CommandSender sender, Component message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(message));
            return;
        }

        successful(player, message, withPrefix);
    }

    public static void restricted(Player player, String message) {
        restricted(player, message, true);
    }

    public static void restricted(Player player, String message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(Component.text(ChatColor.RED + message)));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    public static void restricted(Player player, Component message) {
        restricted(player, message, true);
    }

    public static void restricted(Player player, Component message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(message));
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
    }

    public static void restricted(CommandSender sender, String message) {
        restricted(sender, message, true);
    }

    public static void restricted(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(Component.text(ChatColor.RED + Stealsolo.getInsufficentPermissions())));
            return;
        }

        restricted(player, message, withPrefix);
    }

    public static void restricted(CommandSender sender, Component message) {
        restricted(sender, message, true);
    }

    public static void restricted(CommandSender sender, Component message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(message));
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

    public static void important(Player player, String message) {
        important(player, message, true);
    }

    public static void important(Player player, String message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(Component.text(ChatColor.RED + message)));
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }

    public static void important(Player player, Component message) {
        important(player, message, true);
    }

    public static void important(Player player, Component message, boolean withPrefix) {
        Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
        player.sendMessage(prefix.append(message));
        player.playSound(player.getLocation(), Sound.BLOCK_NETHERITE_BLOCK_STEP, 1, 1);
    }

    public static void important(CommandSender sender, String message) {
        important(sender, message, true);
    }

    public static void important(CommandSender sender, String message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(Component.text(ChatColor.RED + message)));
            return;
        }

        important(player, message, withPrefix);
    }

    public static void important(CommandSender sender, Component message) {
        important(sender, message, true);
    }

    public static void important(CommandSender sender, Component message, boolean withPrefix) {
        if (!(sender instanceof Player player)) {
            Component prefix = withPrefix ? Stealsolo.getPrefix() : Component.empty();
            sender.sendMessage(prefix.append(message));
            return;
        }

        important(player, message, withPrefix);
    }

    public static String convertToString(String message) {
        String step1 = convertLegacyCodes(convertLegacyHex(message));
        return convertLegacyCodes(step1);
    }

    public static Component convertToComponent(String message) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String step1 = convertLegacyCodes(convertLegacyHex(message));
        String step2 = convertLegacyCodes(step1);

        return miniMessage.deserialize(step2);
    }

    private static String convertLegacyHex(String message) {
        Pattern pattern = Pattern.compile("&#([a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String convertLegacyCodes(String message) {
        Map<Character, String> legacyMap = Map.ofEntries(
                Map.entry('0', "black"),
                Map.entry('1', "dark_blue"),
                Map.entry('2', "dark_green"),
                Map.entry('3', "dark_aqua"),
                Map.entry('4', "dark_red"),
                Map.entry('5', "dark_purple"),
                Map.entry('6', "gold"),
                Map.entry('7', "gray"),
                Map.entry('8', "dark_gray"),
                Map.entry('9', "blue"),
                Map.entry('a', "green"),
                Map.entry('b', "aqua"),
                Map.entry('c', "red"),
                Map.entry('d', "light_purple"),
                Map.entry('e', "yellow"),
                Map.entry('f', "white"),
                Map.entry('k', "obfuscated"),
                Map.entry('l', "bold"),
                Map.entry('m', "strikethrough"),
                Map.entry('n', "underlined"),
                Map.entry('o', "italic"),
                Map.entry('r', "reset")
        );

        Pattern legacyPattern = Pattern.compile("[§&]([0-9a-frk-or])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = legacyPattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            char code = matcher.group(1).toLowerCase().charAt(0);
            String tag = legacyMap.get(code);
            if (tag == null) {
                tag = "";
            }
            matcher.appendReplacement(sb, "<" + tag + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}