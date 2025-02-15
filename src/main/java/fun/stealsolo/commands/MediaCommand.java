package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaCommand implements CommandExecutor {
    private static final HashMap<Player, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!Permission.hasPermission(sender, "media")) {
            Message.restricted(sender);
            return false;
        }

        if (args.length != 2) {
            Message.invalid(sender, "Usage: /media <type> <link>");
            return false;
        }

        if (sender instanceof Player p) {
            if (cooldown.containsKey(p)) {
                long timeLeft = (1000L * Stealsolo.getMediaCooldown()) - (System.currentTimeMillis() - cooldown.get(p));
                if (timeLeft > 0) {
                    long minutes = (timeLeft / 1000) / 60;
                    long seconds = (timeLeft / 1000) % 60;
                    String timeMessage = (minutes > 0 ? minutes + " minute" + (minutes > 1 ? "s" : "") + " and " : "") + seconds + " second" + (seconds != 1 ? "s" : "");
                    Message.invalid(sender, "You must wait " + timeMessage + " before sending another media advertisement.", false);
                    return false;
                }
            }
        }

        if (!args[1].toLowerCase().startsWith("https://")) {
            Message.invalid(sender, "Invalid link. Link must start with 'https://'.");
            return false;
        }

        String message;
        String hover;
        switch (args[0].toLowerCase()) {
            case "upload" -> {
                message = Stealsolo.getUploadMsg().replace("%player%", sender.getName()).replace("%link%", args[1]);
                hover = Stealsolo.getUploadHoverMsg().replace("%player%", sender.getName()).replace("%link%", args[1]);
            }
            case "stream" -> {
                message = Stealsolo.getStreamMsg().replace("%player%", sender.getName()).replace("%link%", args[1]);
                hover = Stealsolo.getStreamHoverMsg().replace("%player%", sender.getName()).replace("%link%", args[1]);
            }
            default -> {
                Message.invalid(sender, "Invalid media type. Valid types are 'upload' and 'stream'.");
                return false;
            }
        }

        if (sender instanceof Player p) {
            if (Stealsolo.isPlaceholderAPI()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
                message = PlaceholderAPI.setPlaceholders(offlinePlayer, message);
                hover = PlaceholderAPI.setPlaceholders(offlinePlayer, hover);
            }
        }

        message = convertLegacyHex(message);
        hover = convertLegacyHex(hover);

        message = convertLegacyCodes(message);
        hover = convertLegacyCodes(hover);

        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component messageComponent = miniMessage.deserialize(message)
                .hoverEvent(HoverEvent.showText(miniMessage.deserialize(hover)))
                .clickEvent(ClickEvent.openUrl(args[1]));

        if (sender instanceof Player p) {
            cooldown.put(p, System.currentTimeMillis());
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(messageComponent);
        }
        return true;
    }

    public static String convertLegacyHex(String message) {
        Pattern pattern = Pattern.compile("&#([a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<#" + matcher.group(1) + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String convertLegacyCodes(String message) {
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