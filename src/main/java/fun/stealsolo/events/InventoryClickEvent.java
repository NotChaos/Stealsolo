package fun.stealsolo.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (e.getView().getTitle().equals(ChatColor.GREEN + "Raven likes ducks")) {
            if (e.getCurrentItem() == null) {
                return;
            }
            e.getWhoClicked().sendMessage(ChatColor.GREEN + "You clicked on " + e.getCurrentItem().getItemMeta().getDisplayName());
            e.setCancelled(true);
        }
    }
}
