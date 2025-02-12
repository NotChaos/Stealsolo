package fun.stealsolo.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PluginTC implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                List<String> options = new ArrayList<>();
                options.add("reload");

                return options;
            }
            default -> {
                return List.of();
            }
        }
    }
}
