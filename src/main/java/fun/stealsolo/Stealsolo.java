package fun.stealsolo;

import fun.stealsolo.commands.*;
import fun.stealsolo.tabcompleter.EmptyTC;
import fun.stealsolo.tabcompleter.MediaTC;
import fun.stealsolo.tabcompleter.PayCoinsTC;
import fun.stealsolo.tabcompleter.PluginTC;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stealsolo extends JavaPlugin {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
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
    @Getter
    public static String uploadMsg;
    @Getter
    public static String streamMsg;

    private static void initConfig() {
        Stealsolo.configuration = plugin.getConfig();

        FileConfiguration bukkitConfig = Bukkit.getServer().spigot().getConfig();
        String noPermission = bukkitConfig.getString("messages.no-permission");

        if (noPermission == null) {
            insufficentPermissions = ChatColor.translateAlternateColorCodes('&', configuration.getString("InsufficentPermissions", "&4You do not have permission to use this command."));
        } else {
            insufficentPermissions = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.no-permission", "You do not have permission to use this command."));
        }
        prefix = ChatColor.translateAlternateColorCodes('&', configuration.getString("MessagePrefix", "&4&lStealSolo &f&l| "));
        mediaCooldown = configuration.getInt("media.cooldown");
        uploadMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("media.UploadMessage", "&5Check out a video on YouTube by clicking this message!"));
        streamMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("media.StreamMessage", "&#fdd835Check out a streamer on Twitch by clicking this message!"));
        debug = configuration.getBoolean("debug");
    }

    public static void reloadConfiguration() {
        initConfig();
    }

    @Override
    public void onEnable() {
        long timestamp = System.currentTimeMillis();
        Stealsolo.plugin = this;

        initConfig();
        initEvents();
        initCommands();
        initTabCompleters();

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            Stealsolo.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            plugin.getLogger().warning("PlayerPoints not found! /paycoins will not work.");
        }

        long time = System.currentTimeMillis() - timestamp;
        plugin.getLogger().info("Stealsolo enabled in " + time + "ms");
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
        Objects.requireNonNull(getCommand("media")).setTabCompleter(new MediaTC());
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(new EmptyTC());
    }
}