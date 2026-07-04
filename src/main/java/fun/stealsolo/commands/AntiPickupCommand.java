package fun.stealsolo.commands;

import com.duckydeveloper.util.Message;
import fun.stealsolo.Stealsolo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AntiPickupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.restricted(sender);
            return false;
        }

        if (!Stealsolo.getAntiPickupList().contains(player.getUniqueId().toString())) {
            Stealsolo.getAntiPickupList().add(player.getUniqueId().toString());
            Stealsolo.getConfiguration().set("antipickup.data", Stealsolo.getAntiPickupList());
            Stealsolo.getPlugin().saveConfig();
            Message.successful(sender, Stealsolo.getAntiPickupEnabled());
            return true;
        }

        Stealsolo.getAntiPickupList().remove(player.getUniqueId().toString());
        Stealsolo.getConfiguration().set("antipickup.data", Stealsolo.getAntiPickupList());
        Stealsolo.getPlugin().saveConfig();
        Message.successful(sender, Stealsolo.getAntiPickupDisabled());
        return true;
    }
}
