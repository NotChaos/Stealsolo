package fun.stealsolo.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class NightvisionCommand implements CommandExecutor {
    private final Set<Player> effectList = new HashSet<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return false;
        }

        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /vanish");
            return false;
        }

        Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Raven likes ducks");
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta1 = item.getItemMeta();
        if (meta1 != null) {
            meta1.setDisplayName(ChatColor.YELLOW + "Ducks are funny");
            item.setItemMeta(meta1);
        }

        inventory.setItem( 3, item);

        ItemStack chickenEgg = new ItemStack(Material.CHICKEN_SPAWN_EGG);
        ItemMeta meta = chickenEgg.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Duck Spawn Egg");
            chickenEgg.setItemMeta(meta);
        }
        inventory.setItem(5, chickenEgg);

        p.openInventory(inventory);

        if (effectList.contains(p)) {
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
            effectList.remove(p);
            return false;
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 1));
        effectList.add(p);
        return true;
    }
}
