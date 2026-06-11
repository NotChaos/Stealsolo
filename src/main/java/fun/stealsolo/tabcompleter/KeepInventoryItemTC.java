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
import java.util.stream.Collectors;

public class KeepInventoryItemTC implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        switch (args.length) {
            case 1:
                // Suggest main commands: give, set, check
                completions.add("give");
                completions.add("set");
                completions.add("check");
                return completions;

            case 2:
                String subCommand = args[0].toLowerCase();
                switch (subCommand) {
                    case "set":
                    case "check":
                        return getSuggestedPlayers(args[1]);
                    default:
                        return new ArrayList<>();
                }

            case 3:
                if (args[0].equalsIgnoreCase("set")) {
                    return new ArrayList<>();
                }
                return new ArrayList<>();

            default:
                return new ArrayList<>();
        }
    }

    private List<String> getSuggestedPlayers(String input) {
        if (input.length() < 4) {
            return new ArrayList<>();
        }

        String lowerInput = input.toLowerCase();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(lowerInput))
                .limit(15)
                .collect(Collectors.toList());
    }
}
