package fun.stealsolo;

import fun.stealsolo.commands.NightvisionCommand;
import fun.stealsolo.commands.PayCoinsCommand;
import fun.stealsolo.events.InventoryClickEvent;
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
    public static String permissionPrefix;
    @Getter
    public static String prefix;
    @Getter
    public static PlayerPointsAPI ppAPI;


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

    private void initConfig() {
        Stealsolo.configuration = getConfig();

        insufficentPermissions = configuration.getString("InsufficentPermissions");
        permissionPrefix = configuration.getString("PermissionPrefix");
        prefix = configuration.getString("MessagePrefix");
    }

    private void initEvents() {
        Bukkit.getPluginManager().registerEvents(new InventoryClickEvent(), this);
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("nv")).setExecutor(new NightvisionCommand());
        Objects.requireNonNull(getCommand("paycoins")).setExecutor(new PayCoinsCommand());
    }

    private void initTabCompleters() {
        getCommand("paycoins").setTabCompleter(new PayCoinsTC());
    }
}