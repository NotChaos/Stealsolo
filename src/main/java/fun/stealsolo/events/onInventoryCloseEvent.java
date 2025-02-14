package fun.stealsolo.events;

import fun.stealsolo.Stealsolo;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class onInventoryCloseEvent implements Listener {

    @Getter
    public static final Set<Player> players = new HashSet<>();
    @Getter
    public static final Map<Player, Inventory> inventories = new HashMap<>();

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("TrashGUI") && players.contains((Player) event.getPlayer())) {
            Player p = (Player) event.getPlayer();
            Inventory inventory = event.getInventory();

            inventories.put(p, inventory);

            Bukkit.getScheduler().runTask(Stealsolo.getPlugin(), () -> {
                Inventory inv = Bukkit.createInventory(p, 27, "Delete the trash?");

                ItemStack denyItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                ItemMeta denyMeta = denyItem.getItemMeta();
                denyMeta.setDisplayName(ChatColor.RED + "Delete trash");
                denyItem.setItemMeta(denyMeta);
                inv.setItem(12, denyItem);

                ItemStack infoItem = new ItemStack(Material.PAPER);
                ItemMeta infoMeta = infoItem.getItemMeta();
                infoMeta.setDisplayName(ChatColor.YELLOW + "Are you sure you want to delete the trash?");
                infoMeta.setLore(new ArrayList<>(Arrays.asList(ChatColor.GRAY + "This action cannot be undone.")));
                infoItem.setItemMeta(infoMeta);
                inv.setItem(4, infoItem);

                ItemStack confirmItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                ItemMeta confirmMeta = confirmItem.getItemMeta();
                confirmMeta.setDisplayName(ChatColor.GREEN + "Cancel deletion");
                confirmItem.setItemMeta(confirmMeta);
                inv.setItem(14, confirmItem);

                p.openInventory(inv);
            });
        }
    }
}