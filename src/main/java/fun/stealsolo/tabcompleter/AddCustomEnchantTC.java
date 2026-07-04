package fun.stealsolo.tabcompleter;

import fun.stealsolo.commands.AddCustomEnchantCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AddCustomEnchantTC implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            return null;
        }

        List<String> options = new ArrayList<>();

        switch (args.length) {
            case 1 -> {
                for (AddCustomEnchantCommand.CustomEnchantType type : AddCustomEnchantCommand.CustomEnchantType.values()) {
                    if (type.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        options.add(type.name().toLowerCase());
                    }
                }
            }
            case 2 -> {
                if ("true".startsWith(args[1].toLowerCase())) options.add("true");
                if ("false".startsWith(args[1].toLowerCase())) options.add("false");
            }
            case 3 -> {
                try {
                    AddCustomEnchantCommand.CustomEnchantType type =
                            AddCustomEnchantCommand.CustomEnchantType.valueOf(args[0].toUpperCase());

                    switch (type) {
                        case KNOCKBACK -> {
                            options.add("5.0");
                            options.add("7.5");
                            options.add("10.0");
                        }
                        case DASH -> {
                            options.add("1.0");
                            options.add("1.5");
                            options.add("2.0");
                            options.add("2.5");
                        }
                        case EFFECT -> {
                            options.add("SPEED");
                            options.add("INCREASE_DAMAGE");
                            options.add("STRENGTH");
                            options.add("HASTE");
                            options.add("SLOWNESS");
                            options.add("WEAKNESS");
                            options.add("HEALTH_BOOST");
                            options.add("RESISTANCE");
                            options.add("JUMP");
                            options.add("REGENERATION");
                        }
                    }
                } catch (IllegalArgumentException ignore) {
                }
            }
        }

        return options;
    }
}
