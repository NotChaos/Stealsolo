package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import fun.stealsolo.util.Message;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaCommand implements CommandExecutor {
    private static final HashMap<Player, Long> cooldown = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length == 0) {
            Message.invalid(sender, "Usage: /media <message>");
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
            cooldown.put(p, System.currentTimeMillis());
        }

        String message = String.join(" ", args);
        TextComponent textComponent = new TextComponent();

        Pattern pattern = Pattern.compile("\\[\"(.*?)\",\\s*\"(.*?)\"\\]");
        Matcher matcher = pattern.matcher(message);

        int lastEnd = 0;
        while (matcher.find()) {
            String before = message.substring(lastEnd, matcher.start());
            if (!before.isEmpty()) {
                TextComponent beforeComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', before));
                beforeComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Sent by " + sender.getName() + ". Link can be malicious so check if it is a trusted domain like youtube.com or tiktok.com.")));
                textComponent.addExtra(beforeComponent);
            }

            String linkText = matcher.group(1);
            String linkUrl = matcher.group(2);

            TextComponent linkComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', linkText));
            linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, linkUrl));
            linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(linkUrl)));
            textComponent.addExtra(linkComponent);

            lastEnd = matcher.end();
        }

        if (lastEnd < message.length()) {
            TextComponent afterComponent = new TextComponent(ChatColor.translateAlternateColorCodes('&', message.substring(lastEnd)));
            afterComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Sent by " + sender.getName() + ". Link can be malicious so check if it is a trusted domain like youtube.com or tiktok.com.")));
            textComponent.addExtra(afterComponent);
        }

        Bukkit.spigot().broadcast(textComponent);
        return true;
    }
}