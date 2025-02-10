package fun.stealsolo;

import fun.stealsolo.commands.NightvisionCommand;
import fun.stealsolo.commands.PayCoinsCommand;
import fun.stealsolo.tabcompleter.EmptyTC;
import fun.stealsolo.tabcompleter.PayCoinsTC;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
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

        insufficentPermissions = configuration.getString("InsufficentPermissions");
        prefix = configuration.getString("MessagePrefix");
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
    }

    private void initTabCompleters() {
        if (ppAPI != null) {
            Objects.requireNonNull(getCommand("paycoins")).setTabCompleter(new PayCoinsTC());
        }
        getCommand("nightvision").setTabCompleter(new EmptyTC());
    }

    public static void reloadConfiguration() {
        initConfig();
    }
}