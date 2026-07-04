package fun.stealsolo.events;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class onItemInteractEvent implements Listener {


    @Getter
    private static Plugin plugin;
    @Getter
    private static Logger logger;
    @Getter
    private static boolean debug;

    public onItemInteractEvent(Plugin plugin, boolean debug) {
        onItemInteractEvent.plugin = plugin;
        logger = plugin.getLogger();
        onItemInteractEvent.debug = debug;
    }

    @EventHandler
    public void ItemInteractEvent(PlayerInteractEvent e) {
        if (!Stealsolo.isCurrencyEnabled()) {
            return;
        }

        ItemStack itemStack = e.getItem();
        Player player = e.getPlayer();

        if (itemStack == null || itemStack.getType() == Material.AIR || itemStack.getItemMeta() == null) {
            return;
        }

        if (!Boolean.TRUE.equals(itemStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(Stealsolo.getPlugin(), DuckAPI.getPersistentDataKey() + ".keepInventory"), PersistentDataType.BOOLEAN))) {
            return;
        }

        if (Stealsolo.getDeluxecombatApi().isInCombat(player)) {
            Message.restricted(player, DuckAPI.getLanguageComponent("KeepInventoryVoucher.Use.InCombat"));
            return;
        }

        if (Stealsolo.getKeepinventoryBalance(player.getUniqueId()) == Stealsolo.getCurrencyMax()) {
            Message.restricted(player, Message.replaceInComponent(DuckAPI.getLanguageComponent("KeepInventoryVoucher.Use.MaxBalance"), "{0}", String.valueOf(Stealsolo.getCurrencyMax())));
            return;
        }

        itemStack.setAmount(itemStack.getAmount() - 1);
        Stealsolo.setKeepinventoryBalance(player.getUniqueId(), Stealsolo.getKeepinventoryBalance(player.getUniqueId()) + 1);
        Message.successful(player, Message.replaceInComponent(DuckAPI.getLanguageComponent("KeepInventoryVoucher.Use.Success"), "{0}", String.valueOf(Stealsolo.getKeepinventoryBalance(player.getUniqueId()))));
    }
}
