package fun.stealsolo;

import fun.stealsolo.PlaceholderAPI.StealsoloExpansion;
import fun.stealsolo.commands.*;
import fun.stealsolo.events.*;
import fun.stealsolo.tabcompleter.*;
import fun.stealsolo.util.DamageArea;
import fun.stealsolo.util.Message;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Stealsolo extends JavaPlugin {

    @Getter
    private static Plugin plugin;
    @Getter
    private static Configuration configuration;
    @Getter
    private static Component insufficientPermissions;
    @Getter
    private static Component prefix;
    @Getter
    private static PlayerPointsAPI ppAPI = null;
    @Getter
    private static int mediaCooldown;
    @Getter
    private static boolean debug;
    @Getter
    private static String uploadMsg;
    @Getter
    private static String streamMsg;
    @Getter
    private static String uploadHoverMsg;
    @Getter
    private static String streamHoverMsg;
    @Getter
    private static boolean placeholderAPI;
    @Getter
    private static String paycoinsMessageSender;
    @Getter
    private static String paycoinsMessageRecipient;
    @Getter
    private static Location afkLocation;
    @Getter
    private static String pingMessageSelf;
    @Getter
    private static String pingMessageOthers;
    @Getter
    private static List<String> antiPickupList;
    @Getter
    private static Component antiPickupEnabled;
    @Getter
    private static Component antiPickupDisabled;
    @Getter
    private static Set<DamageArea> damageAreas = new HashSet<>();

    private static void initConfig() {
        getPlugin().saveDefaultConfig();
        Stealsolo.configuration = getPlugin().getConfig();

        FileConfiguration bukkitConfig = Bukkit.getServer().spigot().getConfig();
        String noPermission = bukkitConfig.getString("messages.no-permission");

        if (noPermission == null) {
            insufficientPermissions = Message.convertStringToComponent(configuration.getString("InsufficentPermissions", "&4You do not have permission to use this command."));
        } else {
            insufficientPermissions = Message.convertStringToComponent(configuration.getString("messages.no-permission", "You do not have permission to use this command."));
        }
        prefix = Message.convertStringToComponent(configuration.getString("MessagePrefix", "&4&lStealSolo &f&l| "));
        mediaCooldown = configuration.getInt("media.cooldown");
        uploadMsg = configuration.getString("media.UploadMessage", "&5Check out a video on YouTube by clicking this message!");
        streamMsg = configuration.getString("media.StreamMessage", "&#fdd835Check out a streamer on Twitch by clicking this message!");
        uploadHoverMsg = configuration.getString("media.UploadHoverMessage", "&5Click to watch the video!");
        streamHoverMsg = configuration.getString("media.StreamHoverMessage", "&5Click to watch %player% at %link%!");
        paycoinsMessageSender = configuration.getString("paycoins.PayMessageSender", "&5You have paid &6%amount% &5coins to &6%player%&5.");
        paycoinsMessageRecipient = configuration.getString("paycoins.PayMessageRecipient", "&5You have received &6%amount% &5coins from &6%player%&5.");

        for (String key : Objects.requireNonNull(configuration.getConfigurationSection("DmgBoostAreas")).getKeys(false)) {
            DamageArea area = new DamageArea(
                    configuration.getString("DmgBoostAreas." + key + ".name", "Unnamed Area"),
                    new Location(
                            Bukkit.getWorld(configuration.getString("DmgBoostAreas." + key + ".world", "minecraft:overworld")),
                            configuration.getInt("DmgBoostAreas." + key + ".corner1.x", 0),
                            configuration.getInt("DmgBoostAreas." + key + ".corner1.y", 0),
                            configuration.getInt("DmgBoostAreas." + key + ".corner1.z", 0)),
                    new Location(
                            Bukkit.getWorld(configuration.getString("DmgBoostAreas." + key + ".world", "minecraft:overworld")),
                            configuration.getInt("DmgBoostAreas." + key + ".corner2.x", 0),
                            configuration.getInt("DmgBoostAreas." + key + ".corner2.y", 0),
                            configuration.getInt("DmgBoostAreas." + key + ".corner2.z", 0)),
                    configuration.getDouble("DmgBoostAreas." + key + ".dmg-multiplier", 1.0)
            );

            damageAreas.add(area);
        }

        World world = Bukkit.getWorld(configuration.getString("afk.world", "afk"));

        afkLocation = new Location(world,
                configuration.getDouble("afk.location.x", 0),
                configuration.getDouble("afk.location.y", 100),
                configuration.getDouble("afk.location.z", 0));

        pingMessageSelf = configuration.getString("PingMessageSelf", "&7Your current ping is: &a%ping%&7ms");
        pingMessageOthers = configuration.getString("PingMessageOthers", "&7The ping of %target% is: &a%ping%&7ms");

        antiPickupEnabled = Message.convertStringToComponent(configuration.getString("antipickup.EnabledMessage", "&cAnti-pickup is enabled. You cannot pick up items."));
        antiPickupDisabled = Message.convertStringToComponent(configuration.getString("antipickup.DisabledMessage", "&aAnti-pickup is disabled. You can pick up items."));
        antiPickupList = configuration.getStringList("antipickup.data");

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

            new StealsoloExpansion().register();
        } else {
            plugin.getLogger().warning("PlaceholderAPI not found. Placeholders will not work.");
        }

        plugin.getLogger().info("Dependencies loaded.");
    }

    @Override
    public void onEnable() {
        long timestamp = System.currentTimeMillis();
        plugin = this;

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
        getServer().getPluginManager().registerEvents(new onDamageEvent(), this);
        getServer().getPluginManager().registerEvents(new onInventoryClickEvent(), this);
        getServer().getPluginManager().registerEvents(new onItemPickupEvent(), this);

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
        Objects.requireNonNull(getCommand("afk")).setExecutor(new AfkCommand());
        Objects.requireNonNull(getCommand("antipickup")).setExecutor(new AntiPickupCommand());

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
        Objects.requireNonNull(getCommand("ping")).setTabCompleter(new SimpleTC());
        Objects.requireNonNull(getCommand("afk")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("antipickup")).setTabCompleter(new EmptyTC());

        plugin.getLogger().info("Tab completers registered.");
    }
}