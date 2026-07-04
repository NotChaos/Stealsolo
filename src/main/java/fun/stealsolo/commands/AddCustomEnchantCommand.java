package fun.stealsolo.commands;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class AddCustomEnchantCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.invalid(sender, "Only players can execute this command.");
            return false;
        }

        if (args.length < 2) {
            Message.invalid(sender, "Usage: /addcustomenchant <enchant> <setbonus> <value>");
            return false;
        }

        ItemStack item = player.getItemInHand();

        if (item == null || item.getType().equals(Material.AIR)) {
            Message.invalid(player, "You must be holding an item to add a custom enchantment.");
            return false;
        }

        CustomEnchantType type =  CustomEnchantType.valueOf(args[0].toUpperCase());

        if (type == null) {
            Message.invalid(sender, "Unknown enchantment type.");
            return false;
        }

        ItemMeta meta = item.getItemMeta();

        boolean setbonus;

        try {
            setbonus = Boolean.parseBoolean(args[1]);
        } catch (Exception ignore) {
            Message.invalid(sender, "Usage: /addcustomenchant <type> <enchant> <setbonus> <value>");
            return false;
        }

        String value = args[2];

        if (value == null || value.isEmpty()) {
            Message.invalid(sender, "Value cannot be empty.");
            return false;
        }

        if (value.contains(";")) {
            Message.invalid(sender, "Value cannot contain semicolons.");
            return false;
        }

        meta.getPersistentDataContainer().set(new NamespacedKey(Stealsolo.getPlugin(), setbonus ? DuckAPI.getPersistentDataKey() + ".SetbonusEnchant" : DuckAPI.getPersistentDataKey() + ".CustomEnchant"), PersistentDataType.STRING, type.name() + ";" + value);
        item.setItemMeta(meta);
        return true;
    }

    public static enum CustomEnchantType {
        EFFECT,
        DASH,
        KNOCKBACK
    }
}
