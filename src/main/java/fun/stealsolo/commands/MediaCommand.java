package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
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

public class MediaCommand implements CommandExecutor {
    private static final HashMap<Player, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
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

        message = Message.convert(message);
        hover = Message.convert(hover);

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


}