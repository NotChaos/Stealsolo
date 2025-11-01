package fun.stealsolo.commands;

import fun.stealsolo.Stealsolo;
import com.duckydeveloper.util.Message;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StatisticsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.invalid(sender, "Only players can use this command.");
            return false;
        }

        if (args.length >= 2) {
            Message.invalid(sender, Message.convertStringToComponent("&4Usage: /statistics [player]"));
            return false;
        }

        Inventory gui = Stealsolo.getStatisticsGUI() == null ? null : Stealsolo.getStatisticsGUI().getInventory();
        if (gui == null) {
            Stealsolo.getPlugin().getLogger().severe("Debug: statistics GUI is null");
            Message.invalid(sender, "GUI not available.");
            return false;
        }

        OfflinePlayer target;
        if (args.length == 0) {
            target = Bukkit.getOfflinePlayer(player.getUniqueId());
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
                Message.invalid(sender, "Player " + args[0] + " not found.");
                return false;
            }
        }

        ItemStack[] contents = gui.getContents();

        IntStream.range(0, contents.length).forEach(i -> {
            ItemStack item = contents[i];
            if (item == null) {
                Stealsolo.getPlugin().getLogger().fine("Debug: slot " + i + " empty");
                return;
            }

            if (!item.hasItemMeta()) return;

            ItemMeta meta = item.getItemMeta();
            try {
                if (meta.getItemName() != null && !meta.getItemName().isBlank()) {
                    Stealsolo.getPlugin().getLogger().info("Debug: slot " + i + " original displayName=" + meta.getItemName());
                    meta.setItemName(PlaceholderAPI.setPlaceholders(target, meta.getItemName()));
                }

                if (meta.hasLore()) {
                    List<String> newLore = meta.getLore().stream()
                            .map(line -> PlaceholderAPI.setPlaceholders(target, line))
                            .collect(Collectors.toList());
                    meta.setLore(newLore);
                }

                item.setItemMeta(meta);
                gui.setItem(i, item);
            } catch (Exception e) {
                Stealsolo.getPlugin().getLogger().severe("Error while applying placeholders for slot " + i + "\n" + e);
            }
        });

        player.openInventory(gui);
        return true;
    }
}