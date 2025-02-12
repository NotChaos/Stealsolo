package fun.stealsolo;

import fun.stealsolo.commands.*;
import fun.stealsolo.tabcompleter.EmptyTC;
import fun.stealsolo.tabcompleter.PayCoinsTC;
import fun.stealsolo.tabcompleter.PluginTC;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Stealsolo extends JavaPlugin {

    @Getter
    public static Plugin plugin;
    @Getter
    public static Configuration configuration;
    @Getter
    public static String insufficentPermissions;
    @Getter
    public static String prefix;
    @Getter
    public static PlayerPointsAPI ppAPI;
    @Getter
    public static int mediaCooldown;
    @Getter
    public static boolean debug;


    @Override
    public void onEnable() {
        Stealsolo.plugin = this;

        initConfig();
        initEvents();
        initCommands();
        initTabCompleters();

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            Stealsolo.ppAPI = PlayerPoints.getInstance().getAPI();
        }
    }

    private static void initConfig() {
        Stealsolo.configuration = plugin.getConfig();

        FileConfiguration bukkitConfig = Bukkit.getServer().spigot().getConfig();
        String noPermission = bukkitConfig.getString("messages.no-permission");

        if (noPermission == null) {
            insufficentPermissions = configuration.getString("InsufficentPermissions");
        } else {
            insufficentPermissions = bukkitConfig.getString("messages.no-permission", "You do not have permission to use this command.");
        }
        prefix = configuration.getString("MessagePrefix");
        mediaCooldown = configuration.getInt("MediaCooldown");
        debug = configuration.getBoolean("debug");
    }

    private void initEvents() {
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("nightvision")).setExecutor(new NightvisionCommand());
        if (ppAPI != null) {
            Objects.requireNonNull(getCommand("paycoins")).setExecutor(new PayCoinsCommand());
        } else {
            Objects.requireNonNull(getCommand("paycoins")).unregister(Bukkit.getCommandMap());
        }
        Objects.requireNonNull(getCommand("stealsolo")).setExecutor(new PluginCommand());
        Objects.requireNonNull(getCommand("trash")).setExecutor(new TrashCommand());
        Objects.requireNonNull(getCommand("media")).setExecutor(new MediaCommand());
        Objects.requireNonNull(getCommand("ping")).setExecutor(new PingCommand());
    }

    private void initTabCompleters() {
        if (ppAPI != null) {
            Objects.requireNonNull(getCommand("paycoins")).setTabCompleter(new PayCoinsTC());
        }
        Objects.requireNonNull(getCommand("nightvision")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("stealsolo")).setTabCompleter(new PluginTC());
        Objects.requireNonNull(getCommand("trash")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("media")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(new EmptyTC());
    }

    public static void reloadConfiguration() {
        initConfig();
    }
}