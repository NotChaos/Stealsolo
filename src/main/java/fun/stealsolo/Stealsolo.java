package fun.stealsolo;

import com.duckydeveloper.DuckAPI;
import com.duckydeveloper.util.GUI;
import com.duckydeveloper.util.Message;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import fun.stealsolo.Packetevents.PacketEventsPacketListener;
import fun.stealsolo.PlaceholderAPI.StealsoloExpansion;
import fun.stealsolo.commands.*;
import fun.stealsolo.events.*;
import fun.stealsolo.tabcompleter.*;
import fun.stealsolo.util.*;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import nl.marido.deluxecombat.api.DeluxeCombatAPI;
import nl.marido.deluxecombat.events.CombatlogEvent;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
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
    @Getter
    private static final HashMap<UUID, Integer> killstreaks = new HashMap<>();
    private static final ArrayList<String> killstreakData = new ArrayList<>();
    @Getter
    private static final HashMap<UUID, Integer> keepinventoryBalance = new HashMap<>();
    private static final ArrayList<String> keepinventoryBalanceData = new ArrayList<>();
    @Getter
    private static ItemStack keepInventoryItem;
    @Getter
    private static Economy econ = null;
    @Getter
    private static DeluxeCombatAPI deluxecombatApi;
    @Getter
    private static boolean currencyEnabled;
    @Getter
    private static String currencyName;
    @Getter
    private static String currencySymbol;
    @Getter
    private static int currencyMax;
    @Getter
    private static boolean reviveEnabled;
    @Getter
    private static int reviveHealPercent;
    @Getter
    private static String reviveSound;
    @Getter
    private static List<ConfigParticle> reviveParticles;
    @Getter
    private static DoubleFlag DamageMultiplierFlag;
    @Getter
    private static final List<BattleLocation> battleLocations = new ArrayList<>();
    @Getter
    private static Location spawnLocation;
    @Getter
    private static final HashMap<UUID, UUID> battleRequests = new HashMap<>();

    public static List<Player> activeBattlers() {
        List<Player> activeBattlers = new ArrayList<>();

        battleLocations.forEach(location -> {
            if (location.active()) {
                activeBattlers.add(location.firstPlayer());
                activeBattlers.add(location.secondPlayer());
            }
        });

        return activeBattlers;
    }

    private static void initConfig() {
        plugin.getLogger().info("Loading Stealsolo configuration...");

        plugin.saveDefaultConfig();
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

        plugin.getLogger().info("Quiz questions loaded: " + Quiz.getQuestions().size());


        DuckAPI.DuckAPIBuilder duckAPIBuilder = new DuckAPI.DuckAPIBuilder();
        duckAPIBuilder.setPlugin(plugin);
        duckAPIBuilder.setDebug(debug);
        duckAPIBuilder.setEnableHeadAPIIntegration(true);
        duckAPIBuilder.setBstatsId(31050);
        DuckAPI.init(duckAPIBuilder);

        plugin.getLogger().info("Duck API initialized!");

        statisticsGUI = GUI.parseConfig("StatsGUI").join();

        plugin.getLogger().info("Statistics GUI loaded.");

        deathPenaltyCommand = configuration.getString("DeathPenalty.Command", "eco take %player% 10");
        killRewardCommand = configuration.getString("KillReward.Command", "eco give %player% 15");
        killstreakData.clear();
        List<String> stored = configuration.getStringList("DeathPenalty.KillStreakData");
        killstreakData.addAll(stored);

        for (String data : stored) {
            String[] parts = data.split(":", 2);
            if (parts.length != 2) continue;

            try {
                UUID playerUUID = UUID.fromString(parts[0]);
                int streak = Integer.parseInt(parts[1]);
                killstreaks.put(playerUUID, streak);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("[Stealsolo] Ignoring invalid killstreak entry: " + data);
            }
        }

        try {
            keepInventoryItem = new ItemStack(Material.valueOf(configuration.getString("KeepInventory.Item.Material", "PAPER")));
        } catch (Exception e) {
            keepInventoryItem = new ItemStack(Material.PAPER);
        }

        keepinventoryBalanceData.clear();
        List<String> storedKeepinventoryBalance = configuration.getStringList("KeepInventory.Data");
        keepinventoryBalanceData.addAll(storedKeepinventoryBalance);

        for (String data : storedKeepinventoryBalance) {
            String[] parts = data.split(":", 2);
            if (parts.length != 2) continue;

            try {
                UUID playerUUID = UUID.fromString(parts[0]);
                int streak = Integer.parseInt(parts[1]);
                keepinventoryBalance.put(playerUUID, streak);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("[Stealsolo] Ignoring invalid keepinv balance entry: " + data);
            }
        }

        try {
            keepInventoryItem = new ItemStack(Material.valueOf(configuration.getString("KeepInventory.Item.Material", "PAPER")));
        } catch (Exception e) {
            keepInventoryItem = new ItemStack(Material.PAPER);
        }

        ItemMeta itemMeta = keepInventoryItem.getItemMeta();
        itemMeta.setDisplayName(Message.convertStringToLegacy(configuration.getString("KeepInventory.Item.Name", "&aKeep Inventory Token")));
        itemMeta.setLore(Message.convertStringListToLegacy(configuration.getStringList("KeepInventory.Item.Lore")));
        itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, DuckAPI.getPersistentDataKey() + ".keepInventory"), PersistentDataType.BOOLEAN, true);

        keepInventoryItem.setItemMeta(itemMeta);


        currencyEnabled = configuration.getBoolean("KeepInventory.DigitalCurrency.Enabled", true);
        currencyName = configuration.getString("KeepInventory.DigitalCurrency.Name", "Keepinventory Coins");
        currencySymbol = configuration.getString("KeepInventory.DigitalCurrency.Symbol", "KIC");
        currencyMax = configuration.getInt("KeepInventory.DigitalCurrency.Max", 3);

        reviveEnabled = configuration.getBoolean("KeepInventory.Respawn.Blocked", true);
        reviveHealPercent = configuration.getInt("KeepInventory.Respawn.HealPercent", 50);
        reviveSound = configuration.getString("KeepInventory.Respawn.Sound", "entity.zombie.break_wooden_door");
        reviveParticles = new ArrayList<>();
        List<String> particlesConfig = configuration.getStringList("KeepInventory.Respawn.Particles");
        for (String particleConfig : particlesConfig) {
            String[] parts = particleConfig.split(":");
            if (parts.length != 6) {
                plugin.getLogger().warning("[Stealsolo] Invalid particle configuration: " + particleConfig);
                continue;
            }
            try {
                Particle particle = Particle.valueOf(parts[0].toUpperCase().replace(".", "_"));
                int count = Integer.parseInt(parts[1]);
                double offsetX = Double.parseDouble(parts[2]);
                double offsetY = Double.parseDouble(parts[3]);
                double offsetZ = Double.parseDouble(parts[4]);
                double speed = Double.parseDouble(parts[5]);
                reviveParticles.add(new ConfigParticle(particle, count, offsetX, offsetY, offsetZ, speed));
            } catch (Exception e) {
                plugin.getLogger().warning("[Stealsolo] Invalid particle configuration: " + particleConfig);
            }
        }

        if (!battleLocations.isEmpty()) {
            battleLocations.forEach(battleLocation -> {
                if (battleLocation.firstPlayer() != null && spawnLocation != null) {
                    battleLocation.firstPlayer().teleport(spawnLocation);
                    Message.important(battleLocation.firstPlayer(), DuckAPI.getLanguageComponent("1v1.Reload.KickedFromBattle"));
                }

                if (battleLocation.secondPlayer() != null && spawnLocation != null) {
                    battleLocation.secondPlayer().teleport(spawnLocation);
                    Message.important(battleLocation.firstPlayer(), DuckAPI.getLanguageComponent("1v1.Reload.KickedFromBattle"));
                }
            });
        }

        configuration.getConfigurationSection("1v1").getKeys(false).forEach(key -> {
            Location location = new Location(Bukkit.getWorld(configuration.getString("1v1." + key + ".world", "minecraft:overworld")),
                    configuration.getDouble("1v1." + key + ".x", 0),
                    configuration.getDouble("1v1." + key + ".y", 100),
                    configuration.getDouble("1v1." + key + ".z", 0));

            if (key.toLowerCase().equals("spawn")) {
                spawnLocation = location;
            } else {
                battleLocations.add(new BattleLocation(location, false, null, null));
            }
        });


        plugin.getLogger().info("Configuration loaded.");
    }

    public static void setKillstreak(UUID playerUUID, int killstreak) {
        if (killstreak <= 0) {
            killstreaks.remove(playerUUID);

            // Remove any entries in killstreakData matching this UUID
            Iterator<String> it = killstreakData.iterator();
            while (it.hasNext()) {
                String data = it.next();
                String[] parts = data.split(":", 2);
                if (parts.length != 2) continue;

                try {
                    UUID searchingUUID = UUID.fromString(parts[0]);
                    if (searchingUUID.equals(playerUUID)) {
                        it.remove();
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            configuration.set("DeathPenalty.KillStreakData", new ArrayList<>(killstreakData));
            plugin.saveConfig();
            return;
        }

        // Otherwise update or add the killstreak
        killstreaks.put(playerUUID, killstreak);

        boolean updated = false;
        for (int i = 0; i < killstreakData.size(); i++) {
            String data = killstreakData.get(i);
            String[] parts = data.split(":", 2);
            if (parts.length != 2) continue;

            try {
                UUID searchingUUID = UUID.fromString(parts[0]);
                if (searchingUUID.equals(playerUUID)) {
                    killstreakData.set(i, playerUUID.toString() + ":" + killstreak);
                    updated = true;
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (!updated) {
            killstreakData.add(playerUUID.toString() + ":" + killstreak);
        }

        configuration.set("DeathPenalty.KillStreakData", new ArrayList<>(killstreakData));
        plugin.saveConfig();
    }

    public static int getKillstreak(UUID playerUUID) {
        if (killstreaks.get(playerUUID) == null) {
            return 0;
        }

        return killstreaks.get(playerUUID);
    }

    public static void setKeepinventoryBalance(UUID playerUUID, int balance) {
        if (balance <= 0) {
            keepinventoryBalance.remove(playerUUID);

            // Remove any entries in keepinventoryBalanceData matching this UUID
            Iterator<String> it = keepinventoryBalanceData.iterator();
            while (it.hasNext()) {
                String data = it.next();
                String[] parts = data.split(":", 2);
                if (parts.length != 2) continue;

                try {
                    UUID searchingUUID = UUID.fromString(parts[0]);
                    if (searchingUUID.equals(playerUUID)) {
                        it.remove();
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }

            configuration.set("KeepInventory.Data", new ArrayList<>(keepinventoryBalanceData));
            plugin.saveConfig();
            return;
        }

        // Otherwise update or add the balance
        keepinventoryBalance.put(playerUUID, balance);

        boolean updated = false;
        for (int i = 0; i < keepinventoryBalanceData.size(); i++) {
            String data = keepinventoryBalanceData.get(i);
            String[] parts = data.split(":", 2);
            if (parts.length != 2) continue;

            try {
                UUID searchingUUID = UUID.fromString(parts[0]);
                if (searchingUUID.equals(playerUUID)) {
                    keepinventoryBalanceData.set(i, playerUUID.toString() + ":" + balance);
                    updated = true;
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (!updated) {
            keepinventoryBalanceData.add(playerUUID.toString() + ":" + balance);
        }

        configuration.set("KeepInventory.Data", new ArrayList<>(keepinventoryBalanceData));
        plugin.saveConfig();
    }

    public static int getKeepinventoryBalance(UUID playerUUID) {
        if (keepinventoryBalance.get(playerUUID) == null) {
            return 0;
        }

        return keepinventoryBalance.get(playerUUID);
    }

    public static void reloadConfiguration() {
        plugin.getLogger().info("Reloading Stealsolo configuration...");

        //PacketEvents.getAPI().terminate();
        Bukkit.getScheduler().cancelTasks(plugin);
        /*PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();

        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketEventsPacketListener(), PacketListenerPriority.NORMAL);

        PacketEvents.getAPI().init();*/

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.reloadConfig();
            initConfig();
            initLoops();
        });

        plugin.getLogger().info("Stealsolo configuration reloaded.");
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

        if (!setupEconomy() ) {
            plugin.getLogger().severe(String.format("[%s] - No Vault dependency found!", plugin.getDescription().getName()));
        }

        if (Bukkit.getPluginManager().getPlugin("DeluxeCombat") != null) {
            deluxecombatApi = new DeluxeCombatAPI();
        }

        plugin.getLogger().info("Dependencies loaded.");
    }

    @Override
    public void onLoad() {
        plugin = this;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(plugin));
        PacketEvents.getAPI().load();

        PacketEvents.getAPI().getEventManager().registerListener(
                new PacketEventsPacketListener(), PacketListenerPriority.NORMAL);

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            DoubleFlag damageMultiplierFlag = new DoubleFlag("damage-multiplier");
            registry.register(damageMultiplierFlag);
            DamageMultiplierFlag = damageMultiplierFlag;
        } catch (FlagConflictException e) {
            Flag<?> existingDamageMultiplier = registry.get("damage-multiplier");

            if (existingDamageMultiplier instanceof DoubleFlag) {
                DoubleFlag existingDoubleFlag = (DoubleFlag) existingDamageMultiplier;
                DamageMultiplierFlag = existingDoubleFlag;
            } else {
                getLogger().severe("Flag conflict: 'damage-multiplier' exists but is not a DoubleFlag.");
            }
        }
    }

    @Override
    public void onEnable() {
        long timestamp = System.currentTimeMillis();
        plugin = this;

        initDependencies();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            initConfig();
            initEvents();
            initCommands();
            initTabCompleters();
            initLoops();
        });

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

    private static boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
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
        pm.registerEvents(new onItemInteractEvent(this, debug), this);
        pm.registerEvents(new onCombatLogEvent(this, debug), this);
        pm.registerEvents(new onPlayerUseRespawnEvent(this, debug), this);
        pm.registerEvents(new onDeathInBattleEvent(this, debug), this);

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
        Objects.requireNonNull(getCommand("givekeepinventoryitem")).setExecutor(new KeepInventoryItemCommand());
        Objects.requireNonNull(getCommand("requestbattle")).setExecutor(new BattleCommand());
        Objects.requireNonNull(getCommand("acceptbattle")).setExecutor(new AcceptBattleCommand());
        Objects.requireNonNull(getCommand("denybattle")).setExecutor(new DenyBattleCommand());

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
        Objects.requireNonNull(getCommand("givekeepinventoryitem")).setTabCompleter(new KeepInventoryItemTC());
        Objects.requireNonNull(getCommand("requestbattle")).setTabCompleter(new SimpleTC());
        Objects.requireNonNull(getCommand("acceptbattle")).setTabCompleter(new SimpleTC());
        Objects.requireNonNull(getCommand("denybattle")).setTabCompleter(new SimpleTC());

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
                if (player.getLocation() == null) {
                    return;
                }

                if (player.getWorld() == null) {
                    return;
                }

                try {
                    if (afkArea.isInArea(player.getLocation())) {
                        bossBar.addPlayer(player);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> bossBar.removePlayer(player), 20L);
                    }
                } catch (Exception ignore) {}
            }
        }, 0L, 20L);

        plugin.getLogger().info("Loops initialized.");
    }
}