package fun.stealsolo.events;

import com.duckydeveloper.util.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class onInventoryClickEvent implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();

        if (!onInventoryCloseEvent.getInventories().containsKey(p)) {
            return;
        }

        if (event.getClickedInventory() == null) {
            return;
        }

        if (!event.getView().getTitle().equals("Delete the trash?")) {
            return;
        }

        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getItemMeta() == null) {
            return;
        }

        String delete = ChatColor.RED + "Delete trash";
        String confirm = ChatColor.GREEN + "Cancel deletion";

        ItemMeta itemMeta = currentItem.getItemMeta();
        String displayName = itemMeta.getDisplayName();

        if (delete.equals(displayName)) {
            p.closeInventory();
            Message.successful(p, "Successfully deleted the trash.");
            onInventoryCloseEvent.getInventories().remove(p);
            onInventoryCloseEvent.getPlayers().remove(p);
        } else if (confirm.equals(displayName)) {
            p.closeInventory();
            p.openInventory(onInventoryCloseEvent.getInventories().get(p));
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1.0F, 1.0F);
        }

        event.setCancelled(true);
    }
}