package fun.stealsolo.PlaceholderAPI;

import fun.stealsolo.Stealsolo;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StealsoloExpansion extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", Stealsolo.getPlugin().getPluginMeta().getAuthors());
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "stealsolo";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @NotNull
    public String getVersion() {
        return Stealsolo.getPlugin().getPluginMeta().getVersion();
    }

    @Override
    public boolean canRegister() {
        return true;
    }


    @Override
    public String onRequest(@NotNull OfflinePlayer player, @NotNull String params) {
        switch (params) {
            case "ping" -> {
                Player p = Bukkit.getPlayer(player.getUniqueId());

                if (p == null) {
                    return null;
                }

                return String.valueOf(p.getPing());
            }
            case "keepinventory_balance" -> {
                return String.valueOf(Stealsolo.getKeepinventoryBalance(player.getUniqueId()));
            }
            default -> {
                return null;
            }
        }
    }
}