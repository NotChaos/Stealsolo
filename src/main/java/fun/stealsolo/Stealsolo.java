package fun.stealsolo;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.GUI;
import com.duckydeveloper.util.Message;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fun.stealsolo.Packetevents.PacketEventsPacketListener;
import fun.stealsolo.PlaceholderAPI.StealsoloExpansion;
import fun.stealsolo.commands.*;
import fun.stealsolo.events.*;
import fun.stealsolo.tabcompleter.*;
import fun.stealsolo.util.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Stealsolo extends JavaPlugin {

    @Getter
    private static JavaPlugin plugin;
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
    private static final Set<DamageArea> damageAreas = new HashSet<>();
    @Getter
    private static Area afkArea;
    @Getter
    private static String participationRewardCommand;
    @Getter
    private static String correctAnswerRewardCommand;
    @Getter
    private static List<Component> questionMessage;
    @Getter
    private static List<Component> answeredMessage;
    @Getter
    private static List<Component> unansweredMessage;
    private static int quizCooldown = 120;
    private static int quizProgression = 0;
    @Getter
    private static GUI statisticsGUI;
    @Getter
    private static String deathPenaltyCommand;
    @Getter
    private static String killRewardCommand;

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

        damageAreas.clear();

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

        afkArea = new Area("Coin Area",
                new Location(Bukkit.getWorld(configuration.getString("CoinArea.corner1.world", "minecraft:overworld")),
                        configuration.getInt("CoinArea.corner1.x", 100),
                        configuration.getInt("CoinArea.corner1.y", 60),
                        configuration.getInt("CoinArea.corner1.z", 100)),

                new Location(Bukkit.getWorld(configuration.getString("CoinArea.corner2.world", "minecraft:overworld")),
                        configuration.getInt("CoinArea.corner2.x", 200),
                        configuration.getInt("CoinArea.corner2.y", 100),
                        configuration.getInt("CoinArea.corner2.z", 200))
        );

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

        ConfigurationSection quizSection = configuration.getConfigurationSection("CoinArea.Quiz");
        if (quizSection == null) {
            plugin.getLogger().warning("[Stealsolo] The configuration section 'CoinArea.Quiz' is missing in config.yml!");
        } else {
            quizCooldown = quizSection.getInt("Cooldown", 120);
            participationRewardCommand = quizSection.getString("ParticipationReward", "eco give %player% 5");
            correctAnswerRewardCommand = quizSection.getString("CorrectAnswerReward", "eco give %player% 20");
            questionMessage = Message.convertStringListToComponentList(quizSection.getStringList("QuestionMessage"));
            answeredMessage = Message.convertStringListToComponentList(quizSection.getStringList("AnsweredMessage"));
            unansweredMessage = Message.convertStringListToComponentList(quizSection.getStringList("UnansweredMessage"));

            Quiz.getQuestions().clear();

            for (String key : quizSection.getKeys(false)) {
                ConfigurationSection questionSection = quizSection.getConfigurationSection(key);
                if (questionSection == null) continue;

                List<String> answers = new ArrayList<>(questionSection.getStringList("Answers"));
                answers.replaceAll(String::toLowerCase);

                QuizQuestion question = new QuizQuestion(
                        key,
                        questionSection.getString("Question"),
                        answers
                );

                Quiz.getQuestions().add(question);
            }

            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                Quiz.randomizeQuestion();
                Quiz.sendQuestion();
            }, 0L, quizCooldown * 20L);
        }

        DuckAPI.DuckAPIBuilder duckAPIBuilder = new DuckAPI.DuckAPIBuilder();
        duckAPIBuilder.setPlugin(plugin);
        duckAPIBuilder.setDebug(debug);
        duckAPIBuilder.setEnableHeadAPIIntegration(true);
        DuckAPI.init(duckAPIBuilder);

        statisticsGUI = GUI.parseConfig("StatsGUI");

        deathPenaltyCommand = configuration.getString("DeathPenalty.Command", "eco take %player% 10");
        killRewardCommand = configuration.getString("KillReward.Command", "eco give %player% 15");

        plugin.getLogger().info("Configuration loaded.");
    }

    public static void reloadConfiguration() {
        //PacketEvents.getAPI().terminate();
        Bukkit.getScheduler().cancelTasks(plugin);
        /*PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();

        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketEventsPacketListener(), PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().init();*/


        plugin.reloadConfig();
        initConfig();
        initLoops();
    }

    public static void initDependencies() {
        if (Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            PacketEvents.getAPI().init();
        } else {
            plugin.getLogger().warning("packetevents not found! This might break the plugin.");
        }

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
    public void onLoad() {
        plugin = this;

        if (Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
            PacketEvents.getAPI().load();

            PacketEvents.getAPI().getEventManager().registerListener(
                    new PacketEventsPacketListener(), PacketListenerPriority.NORMAL);
        } else {
            plugin.getLogger().warning("packetevents not found! This might break the plugin.");
        }
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
        initLoops();

        long time = System.currentTimeMillis() - timestamp;
        plugin.getLogger().info("Stealsolo enabled in " + time + "ms");
    }

    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            PacketEvents.getAPI().terminate();
        }
        plugin.getLogger().info("Stealsolo plugin disabled.");
    }

    private void initEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new onInventoryCloseEvent(), this);
        pm.registerEvents(new onPlayerQuitEvent(), this);
        pm.registerEvents(new onDamageEvent(), this);
        pm.registerEvents(new onInventoryClickEvent(), this);
        pm.registerEvents(new onItemPickupEvent(), this);
        pm.registerEvents(new onAsyncChatEvent(), this);
        pm.registerEvents(new onPlayerDeathEvent(this, debug), this);

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
        Objects.requireNonNull(getCommand("coinarea")).setExecutor(new AfkCommand());
        Objects.requireNonNull(getCommand("antipickup")).setExecutor(new AntiPickupCommand());
        Objects.requireNonNull(getCommand("statistics")).setExecutor(new StatisticsCommand());

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
        Objects.requireNonNull(getCommand("coinarea")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("antipickup")).setTabCompleter(new EmptyTC());
        Objects.requireNonNull(getCommand("statistics")).setTabCompleter(new SimpleTC());

        plugin.getLogger().info("Tab completers registered.");
    }

    private static void initLoops() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (quizProgression + 1 >= quizCooldown) {
                quizProgression = 0;
                return;
            }

            quizProgression++;
            int remaining = quizCooldown - quizProgression;
            if (remaining < 0) remaining = 0;

            String title = Message.convertStringToLegacy(configuration.getString("CoinArea.Quiz.BossBar", "Next question in %time%")
                    .replace("%time%", String.valueOf(remaining)));
            BossBar bossBar = Bukkit.createBossBar(title, BarColor.YELLOW, BarStyle.SEGMENTED_20);

            float progress = 1f - (quizProgression / (float) quizCooldown);
            if (progress < 0f) progress = 0f;
            if (progress > 1f) progress = 1f;
            bossBar.setProgress(progress);
            bossBar.setVisible(true);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (afkArea.isInArea(player.getLocation())) {
                    bossBar.addPlayer(player);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> bossBar.removePlayer(player), 20L);
                }
            }
        }, 0L, 20L);

        plugin.getLogger().info("Loops initialized.");
    }
}