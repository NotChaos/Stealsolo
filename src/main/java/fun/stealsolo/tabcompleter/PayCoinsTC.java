package fun.stealsolo.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PayCoinsTC implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                List<String> players = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
                players.remove(sender.getName());

                return players;
            }
            case 2 -> {
                return List.of("100", "200", "300", "400", "500");
            }
            default -> {
                return List.of();
            }
        }
    }
}
