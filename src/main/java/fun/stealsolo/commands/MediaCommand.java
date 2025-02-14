package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
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
        switch (args[0].toLowerCase()) {
            case "upload" -> message = Stealsolo.getUploadMsg();
            case "stream" -> message = Stealsolo.getStreamMsg();
            default -> {
                Message.invalid(sender, "Invalid media type. Valid types are 'upload' and 'stream'.");
                return false;
            }
        }

        TextComponent textComponent = new TextComponent(message);
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, args[1]));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(sender.getName() + " has send the link: " + args[1])));

        if (sender instanceof Player p) {
            cooldown.put(p, System.currentTimeMillis());
        }
        Bukkit.spigot().broadcast(textComponent);
        return true;
    }
}