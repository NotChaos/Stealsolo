package fun.stealsolo;

import fun.stealsolo.commands.*;
import fun.stealsolo.events.onInventoryClickEvent;
import fun.stealsolo.events.onInventoryCloseEvent;
import fun.stealsolo.events.onPlayerQuitEvent;
import fun.stealsolo.tabcompleter.EmptyTC;
import fun.stealsolo.tabcompleter.MediaTC;
import fun.stealsolo.tabcompleter.PayCoinsTC;
import fun.stealsolo.tabcompleter.PluginTC;
import lombok.Getter;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
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
    public static PlayerPointsAPI ppAPI = null;
    @Getter
    public static int mediaCooldown;
    @Getter
    public static boolean debug;
    @Getter
    public static String uploadMsg;
    @Getter
    public static String streamMsg;
    @Getter
    public static String uploadHoverMsg;
    @Getter
    public static String streamHoverMsg;
    @Getter
    public static boolean placeholderAPI;
    @Getter
    public static String paycoinsMessageSender;
    @Getter
    public static String paycoinsMessageRecipient;
    @Getter
    public static Location moshpitCorner1;
    @Getter
    public static Location moshpitCorner2;
    @Getter
    public static int moshpitDmgMultiplier;

    private static void initConfig() {
        plugin.saveDefaultConfig();
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
        uploadMsg = configuration.getString("media.UploadMessage", "&5Check out a video on YouTube by clicking this message!");
        streamMsg = configuration.getString("media.StreamMessage", "&#fdd835Check out a streamer on Twitch by clicking this message!");
        uploadHoverMsg = configuration.getString("media.UploadHoverMessage", "&5Click to watch the video!");
        streamHoverMsg = configuration.getString("media.StreamHoverMessage", "&5Click to watch %player% at %link%!");
        paycoinsMessageSender = configuration.getString("paycoins.PayMessageSender", "&5You have paid &6%amount% &5coins to &6%player%&5.");
        paycoinsMessageRecipient = configuration.getString("paycoins.PayMessageRecipient", "&5You have received &6%amount% &5coins from &6%player%&5.");


        moshpitCorner1 = new Location(Bukkit.getWorld(configuration.getString("moshpit.corner1.world")), configuration.getInt("moshpit.corner1.x"), configuration.getInt("moshpit.corner1.y"), configuration.getInt("moshpit.corner1.z"));
        moshpitCorner2 = new Location(Bukkit.getWorld(configuration.getString("moshpit.corner2.world")), configuration.getInt("moshpit.corner2.x"), configuration.getInt("moshpit.corner2.y"), configuration.getInt("moshpit.corner2.z"));
        moshpitDmgMultiplier = configuration.getInt("moshpit.dmg-multiplier", 2);

        debug = configuration.getBoolean("debug");

        plugin.getLogger().info("Configuration loaded.");
    }

    public static void reloadConfiguration() {
        plugin.reloadConfig();
        initConfig();
    }

    public static void initDependencies() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            Bukkit.getLogger().info("PlayerPoints found! /paycoins will be enabled.");
            Stealsolo.ppAPI = PlayerPoints.getInstance().getAPI();
        } else {
            plugin.getLogger().warning("PlayerPoints not found! /paycoins will not work.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderAPI = true;
        } else {
            plugin.getLogger().warning("PlaceholderAPI not found. Placeholders will not work.");
        }

        plugin.getLogger().info("Dependencies loaded.");
    }

    @Override
    public void onEnable() {
        long timestamp = System.currentTimeMillis();
        Stealsolo.plugin = this;

        initDependencies();
        initConfig();
        initEvents();
        initCommands();
        initTabCompleters();

        long time = System.currentTimeMillis() - timestamp;
        plugin.getLogger().info("Stealsolo enabled in " + time + "ms");
    }

    private void initEvents() {
        getServer().getPluginManager().registerEvents(new onInventoryCloseEvent(), this);
        getServer().getPluginManager().registerEvents(new onInventoryClickEvent(), this);
        getServer().getPluginManager().registerEvents(new onPlayerQuitEvent(), this);

        plugin.getLogger().info("Events registered.");
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("nightvision")).setExecutor(new NightvisionCommand());
        if (ppAPI != null) {
            Objects.requireNonNull(getCommand("paycoins")).setExecutor(new PayCoinsCommand());
        } else {
            Objects.requireNonNull(getCommand("paycoins")).setExecutor(new DisabledCommand());
        }
        Objects.requireNonNull(getCommand("stealsolo")).setExecutor(new PluginCommand());
        Objects.requireNonNull(getCommand("trash")).setExecutor(new TrashCommand());
        Objects.requireNonNull(getCommand("media")).setExecutor(new MediaCommand());
        Objects.requireNonNull(getCommand("ping")).setExecutor(new PingCommand());

        plugin.getLogger().info("Commands registered.");
    }

    private void initTabCompleters() {
        if (ppAPI != null) {
            Objects.requireNonNull(getCommand("paycoins")).setTabCompleter(new PayCoinsTC());
        } else {
            Objects.requireNonNull(getCommand("paycoins")).setTabCompleter(new EmptyTC());
        }
        Objects.requireNonNull(getCommand("nightvision")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("stealsolo")).setTabCompleter(new PluginTC());
        Objects.requireNonNull(getCommand("trash")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("media")).setTabCompleter(new MediaTC());
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(new EmptyTC());

        plugin.getLogger().info("Tab completers registered.");
    }
}