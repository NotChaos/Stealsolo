package fun.stealsolo.commands;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
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

import java.lang.reflect.Method;
import java.util.List;
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

        Inventory template = Stealsolo.getStatisticsGUI() == null ? null : Stealsolo.getStatisticsGUI().getInventory();
        if (template == null) {
            Stealsolo.getPlugin().getLogger().severe("Debug: statistics GUI is null");
            Message.invalid(sender, "GUI not available.");
            return false;
        }

        OfflinePlayer target;
        if (args.length == 0) {
            target = Bukkit.getOfflinePlayer(player.getUniqueId());
        } else {
            target = Bukkit.getOfflinePlayer(args[0]);
            if (!target.hasPlayedBefore() && !target.isOnline()) {
                Message.invalid(sender, "Player " + args[0] + " not found.");
                return false;
            }
        }

        String title = null;
        try {
            Object holder = template.getHolder();
            if (holder != null) {
                try {
                    Method m = holder.getClass().getMethod("getTitle");
                    Object t = m.invoke(holder);
                    if (t instanceof String) title = (String) t;
                } catch (NoSuchMethodException ignored) {
                }
                if (title == null) {
                    try {
                        Method m = holder.getClass().getMethod("getName");
                        Object t = m.invoke(holder);
                        if (t instanceof String) title = (String) t;
                    } catch (NoSuchMethodException ignored) {
                    }
                }
                if (title == null) {
                    try {
                        Method m = holder.getClass().getMethod("getDisplayName");
                        Object t = m.invoke(holder);
                        if (t instanceof String) title = (String) t;
                    } catch (NoSuchMethodException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            Stealsolo.getPlugin().getLogger().fine("Could not determine GUI title via reflection: " + e);
        }

        if (title == null) title = "Statistics";

        Inventory gui = Bukkit.createInventory(null, template.getSize(), title);

        ItemStack[] contents = template.getContents();

        IntStream.range(0, contents.length).forEach(i -> {
            ItemStack item = contents[i];
            if (item == null) {
                Stealsolo.getPlugin().getLogger().fine("Debug: slot " + i + " empty");
                return;
            }

            ItemStack clone = item.clone();

            if (!clone.hasItemMeta()) {
                gui.setItem(i, clone);
                return;
            }

            ItemMeta meta = clone.getItemMeta();
            try {
                if (meta.getDisplayName() != null && !meta.getDisplayName().isBlank()) {
                    meta.setDisplayName(PlaceholderAPI.setPlaceholders(target, meta.getDisplayName()));
                }

                if (meta.hasLore()) {
                    List<String> oldLore = meta.getLore();
                    if (oldLore != null) {
                        List<String> newLore = oldLore.stream()
                                .map(line -> PlaceholderAPI.setPlaceholders(target, line))
                                .collect(Collectors.toList());
                        meta.setLore(newLore);
                    }
                }

                clone.setItemMeta(meta);
                gui.setItem(i, clone);
            } catch (Exception e) {
                Stealsolo.getPlugin().getLogger().severe("Error while applying placeholders for slot " + i + "\n" + e);
            }
        });

        player.openInventory(gui);
        return true;
    }
}