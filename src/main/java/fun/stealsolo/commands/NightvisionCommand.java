package fun.stealsolo.commands;

import fun.stealsolo.util.Message;
import fun.stealsolo.util.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class NightvisionCommand implements CommandExecutor {
    private final Set<Player> effectList = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player p)) {
            Message.invalid(sender, "Only players can use this command.");
            return false;
        }

        if (!Permission.hasPermission(p, "nightvision")) {
            Message.restricted(p);
            return false;
        }

        if (args.length != 0) {
            Message.invalid(sender, "Usage: /nv");
            return false;
        }

        if (effectList.contains(p)) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            p.sendMessage(ChatColor.GRAY + "You no longer have Night Vision");
            p.sendActionBar(ChatColor.GRAY + "You no longer have Night Vision");
            effectList.remove(p);
            return false;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1));
        p.sendMessage(ChatColor.GREEN + "You now have Night Vision");
        p.sendActionBar(ChatColor.GREEN + "You now have Night Vision");
        effectList.add(p);
        return true;
    }
}
