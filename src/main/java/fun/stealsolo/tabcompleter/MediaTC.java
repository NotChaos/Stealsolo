package fun.stealsolo.tabcompleter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MediaTC implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                List<String> options = new ArrayList<>();
                options.add("upload");
                options.add("stream");

                return options;
            }
            case 2 -> {
                return List.of("https://youtube.com/", "https://tiktok.com/", "https://twitch.com/");
            }
            default -> {
                return List.of();
            }
        }
    }
}
