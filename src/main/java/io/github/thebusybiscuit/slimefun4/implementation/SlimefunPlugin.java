package io.github.thebusybiscuit.slimefun4.implementation;

import io.github.starwishsama.extra.NUpdater;
import io.github.starwishsama.extra.ProtectionChecker;
import io.github.starwishsama.extra.StarWishUtil;
import io.github.thebusybiscuit.cscorelib2.config.Config;
import io.github.thebusybiscuit.cscorelib2.math.DoubleHandler;
import io.github.thebusybiscuit.cscorelib2.protection.ProtectionManager;
import io.github.thebusybiscuit.cscorelib2.reflection.ReflectionUtils;
import io.github.thebusybiscuit.slimefun4.api.MinecraftVersion;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.SlimefunBranch;
import io.github.thebusybiscuit.slimefun4.api.gps.GPSNetwork;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.SlimefunRegistry;
import io.github.thebusybiscuit.slimefun4.core.commands.SlimefunCommand;
import io.github.thebusybiscuit.slimefun4.core.networks.NetworkManager;
import io.github.thebusybiscuit.slimefun4.core.services.*;
import io.github.thebusybiscuit.slimefun4.core.services.github.GitHubService;
import io.github.thebusybiscuit.slimefun4.core.services.plugins.ThirdPartyPluginService;
import io.github.thebusybiscuit.slimefun4.core.services.profiler.SlimefunProfiler;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientAltar;
import io.github.thebusybiscuit.slimefun4.implementation.items.altar.AncientPedestal;
import io.github.thebusybiscuit.slimefun4.implementation.items.backpacks.Cooler;
import io.github.thebusybiscuit.slimefun4.implementation.items.electric.reactors.Reactor;
import io.github.thebusybiscuit.slimefun4.implementation.items.tools.GrapplingHook;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.SeismicAxe;
import io.github.thebusybiscuit.slimefun4.implementation.items.weapons.VampireBlade;
import io.github.thebusybiscuit.slimefun4.implementation.listeners.*;
import io.github.thebusybiscuit.slimefun4.implementation.resources.GEOResourcesSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.PostSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.ResearchSetup;
import io.github.thebusybiscuit.slimefun4.implementation.setup.SlimefunItemSetup;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.ArmorTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.SlimefunStartupTask;
import io.github.thebusybiscuit.slimefun4.implementation.tasks.TickerTask;
import me.mrCookieSlime.CSCoreLibPlugin.CSCoreLib;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AContainer;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.AGenerator;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.UniversalBlockMenu;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * This is the main class of Slimefun.
 * This is where all the magic starts, take a look around.
 * Feel like home.
 *
 * @author TheBusyBiscuit
 *
 */
public final class SlimefunPlugin extends JavaPlugin implements SlimefunAddon {

    private static SlimefunPlugin instance;

    private MinecraftVersion minecraftVersion = MinecraftVersion.UNKNOWN;
    private boolean isNewlyInstalled = false;

    private final SlimefunRegistry registry = new SlimefunRegistry();
    private final TickerTask ticker = new TickerTask();
    private final SlimefunCommand command = new SlimefunCommand(this);

    // Services - Systems that fulfill certain tasks, treat them as a black box
    private final CustomItemDataService itemDataService = new CustomItemDataService(this, "slimefun_item");
    private final BlockDataService blockDataService = new BlockDataService(this, "slimefun_block");
    private final CustomTextureService textureService = new CustomTextureService(new Config(this, "item-models.yml"));
    private final GitHubService gitHubService = new GitHubService("TheBusyBiscuit/Slimefun4");
    private final UpdaterService updaterService = new UpdaterService(this, getDescription().getVersion(), getFile());
    private final MetricsService metricsService = new MetricsService(this);
    private final AutoSavingService autoSavingService = new AutoSavingService();
    private final BackupService backupService = new BackupService();
    private final PermissionsService permissionsService = new PermissionsService(this);
    private final PerWorldSettingsService worldSettingsService = new PerWorldSettingsService(this);
    private final ThirdPartyPluginService thirdPartySupportService = new ThirdPartyPluginService(this);
    private final MinecraftRecipeService recipeService = new MinecraftRecipeService(this);
    private final SlimefunProfiler profiler = new SlimefunProfiler();
    private LocalizationService local;
    private NUpdater updater;

    private GPSNetwork gpsNetwork;
    private NetworkManager networkManager;
    private ProtectionManager protections;

    // Important config files for Slimefun
    private final Config config = new Config(this);
    private final Config items = new Config(this, "Items.yml");
    private final Config researches = new Config(this, "Researches.yml");

    // Listeners that need to be accessed elsewhere
    private final GrapplingHookListener grapplingHookListener = new GrapplingHookListener();
    private final BackpackListener backpackListener = new BackpackListener();
    private final SlimefunBowListener bowListener = new SlimefunBowListener();

    public SlimefunPlugin() {
        super();
    }

    public SlimefunPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        minecraftVersion = MinecraftVersion.UNIT_TEST;
    }


    @Override
    public void onEnable() {
        instance = this;
        if (minecraftVersion == MinecraftVersion.UNIT_TEST) {
            local = new LocalizationService(this, "", null);
            gpsNetwork = new GPSNetwork();
            networkManager = new NetworkManager(200);
            command.register();
            registry.load(config);
        } else if (getServer().getPluginManager().isPluginEnabled("CS-CoreLib")) {
            long timestamp = System.nanoTime();

            StarWishUtil.suggestPaper(this);

            // We wanna ensure that the Server uses a compatible version of Minecraft
            if (isVersionUnsupported()) {
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            // Disabling backwards-compatibility for fresh Slimefun installs
            if (!new File("data-storage/Slimefun").exists()) {
                config.setValue("options.backwards-compatibility", false);
                config.save();

                isNewlyInstalled = true;
            }

            // Creating all necessary Folders
            getLogger().log(Level.INFO, "正在创建文件夹...");
            createDirectories();
            registry.load(config);

            // Set up localization
            getLogger().log(Level.INFO, "正在加载语言文件...");
            local = new LocalizationService(this, config.getString("options.chat-prefix"), config.getString("options.language"));

            // Setting up Networks
            gpsNetwork = new GPSNetwork();

            int networkSize = config.getInt("networks.max-size");

            if (networkSize < 1) {
                getLogger().log(Level.WARNING, "'networks.max-size' 大小设置错误! 它必须大于1, 而你设置的是: {0}", networkSize);
                networkSize = 1;
            }

            networkManager = new NetworkManager(networkSize);

            // Setting up bStats
            new Thread(metricsService::start, "Slimefun Metrics").start();

            // 魔改的自动更新服务
            // 自动选择分支
            NUpdater.autoSelectBranch(this);

            // Registering all GEO Resources
            getLogger().log(Level.INFO, "加载矿物资源...");
            GEOResourcesSetup.setup();

            getLogger().log(Level.INFO, "加载自定义标签...");
            loadTags();

            getLogger().log(Level.INFO, "加载物品...");
            loadItems();

            getLogger().log(Level.INFO, "加载研究项目...");
            loadResearches();

            registry.setResearchingEnabled(getResearchCfg().getBoolean("enable-researching"));
            PostSetup.setupWiki();

            // All Slimefun Listeners
            getLogger().log(Level.INFO, "正在注册监听器...");
            registerListeners();

            // Initiating various Stuff and all items with a slight delay (0ms after the Server finished loading)
            runSync(new SlimefunStartupTask(this, () -> {
                protections = new ProtectionManager(getServer());
                textureService.register(registry.getAllSlimefunItems(), true);
                permissionsService.register(registry.getAllSlimefunItems(), true);

                // This try/catch should prevent buggy Spigot builds from blocking item loading
                try {
                    recipeService.refresh();
                } catch (Exception | LinkageError x) {
                    getLogger().log(Level.SEVERE, x, () -> "An Exception occured while iterating through the Recipe list on Minecraft Version " + minecraftVersion.getName() + " (Slimefun v" + getVersion() + ")");
                }
            }), 0);

            // Setting up the command /sf and all subcommands
            command.register();

            // Armor Update Task
            if (config.getBoolean("options.enable-armor-effects")) {
                boolean radioactiveFire = config.getBoolean("options.burn-players-when-radioactive");
                getServer().getScheduler().runTaskTimerAsynchronously(this, new ArmorTask(radioactiveFire), 0L, config.getInt("options.armor-update-interval") * 20L);
            }

            autoSavingService.start(this, config.getInt("options.auto-save-delay-in-minutes"));
            ticker.start(this);
            thirdPartySupportService.start();
            gitHubService.start(this);

            // Hooray!
            getLogger().log(Level.INFO, "Slimefun 完成加载, 耗时 {0}", getStartupTime(timestamp));

            if (config.getBoolean("options.auto-update") || config.getBoolean("options.update-check")) {
                if (NUpdater.getBranch() == SlimefunBranch.DEVELOPMENT || NUpdater.getBranch() == SlimefunBranch.STABLE) {
                    updater = new NUpdater();
                    Bukkit.getServer().getScheduler().runTaskAsynchronously(instance, updater::checkUpdate);
                }
            }

        } else {
            instance = null;

            getLogger().log(Level.INFO, "#################### - INFO - ####################");
            getLogger().log(Level.INFO, " ");
            getLogger().log(Level.INFO, "Slimefun 未被加载.");
            getLogger().log(Level.INFO, "你没有安装前置 CS-CoreLib.");
            getLogger().log(Level.INFO, "请到以下链接手动下载安装:");
            getLogger().log(Level.INFO, "https://thebusybiscuit.github.io/builds/TheBusyBiscuit/CS-CoreLib/master/");

            getCommand("slimefun").setExecutor((sender, cmd, label, args) -> {
                sender.sendMessage("你没有安装前置 CS-CoreLib, Slimefun 已被禁用.");
                sender.sendMessage("https://thebusybiscuit.github.io/builds/TheBusyBiscuit/CS-CoreLib/master/");
                return true;
            });
        }
    }

    @Nonnull
    private String getStartupTime(long timestamp) {
        long ms = (System.nanoTime() - timestamp) / 1000000;

        if (ms > 1000) {
            return DoubleHandler.fixDouble(ms / 1000.0) + "s";
        } else {
            return DoubleHandler.fixDouble(ms) + "ms";
        }
    }

    private boolean isVersionUnsupported() {
        String currentVersion = ReflectionUtils.getVersion();

        if (currentVersion.startsWith("v")) {
            for (MinecraftVersion version : MinecraftVersion.valuesCache) {
                if (version.matches(currentVersion)) {
                    minecraftVersion = version;
                    return false;
                }
            }

            // Looks like you are using an unsupported Minecraft Version
            getLogger().log(Level.SEVERE, "#############################################");
            getLogger().log(Level.SEVERE, "### Slimefun 未被正确安装!");
            getLogger().log(Level.SEVERE, "### 你正在使用不支持的 Minecraft 版本!");
            getLogger().log(Level.SEVERE, "###");
            getLogger().log(Level.SEVERE, "### 你正在使用 Minecraft {0}", ReflectionUtils.getVersion());
            getLogger().log(Level.SEVERE, "### 但 Slimefun v{0} 只支持", getDescription().getVersion());
            getLogger().log(Level.SEVERE, "### Minecraft {0}", String.join(" / ", getSupportedVersions()));
            getLogger().log(Level.SEVERE, "#############################################");
            return true;
        }

        getLogger().log(Level.WARNING, "We could not determine the version of Minecraft you were using ({0})", currentVersion);
        return false;
    }

    @Nonnull
    private Collection<String> getSupportedVersions() {
        List<String> list = new ArrayList<>();

        for (MinecraftVersion version : MinecraftVersion.valuesCache) {
            if (version != MinecraftVersion.UNKNOWN) {
                list.add(version.getName());
            }
        }

        return list;
    }

    @Override
    public void onDisable() {
        // Slimefun never loaded successfully, so we don't even bother doing stuff here
        if (instance() == null || minecraftVersion == MinecraftVersion.UNIT_TEST) {
            return;
        }

        // Cancel all tasks from this plugin immediately
        Bukkit.getScheduler().cancelTasks(this);

        // Finishes all started movements/removals of block data
        ticker.halt();
        ticker.run();

        PlayerProfile.iterator().forEachRemaining(profile -> {
            if (profile.isDirty()) {
                profile.save();
            }
        });

        // Save all registered Worlds
        for (Map.Entry<String, BlockStorage> entry : getRegistry().getWorlds().entrySet()) {
            try {
                entry.getValue().saveAndRemove();
            } catch (Exception x) {
                getLogger().log(Level.SEVERE, x, () -> "An Error occurred while saving Slimefun-Blocks in World '" + entry.getKey() + "' for Slimefun " + getVersion());
            }
        }

        for (UniversalBlockMenu menu : registry.getUniversalInventories().values()) {
            menu.save();
        }

        // Create a new backup zip
        backupService.run();

        metricsService.cleanUp();

        // Prevent Memory Leaks
        // These static Maps should be removed at some point...
        AContainer.processing = null;
        AContainer.progress = null;

        AGenerator.processing = null;
        AGenerator.progress = null;

        Reactor.processing = null;
        Reactor.progress = null;

        instance = null;

        // Close all inventories on the server to prevent item dupes
        // (Incase some idiot uses /reload)
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.closeInventory();
        }
    }

    private void createDirectories() {
        String[] storageFolders = {"Players", "blocks", "stored-blocks", "stored-inventories", "stored-chunks", "universal-inventories", "waypoints", "block-backups"};
        String[] pluginFolders = {"scripts", "error-reports", "cache/github", "world-settings"};

        for (String folder : storageFolders) {
            File file = new File("data-storage/Slimefun", folder);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        for (String folder : pluginFolders) {
            File file = new File("plugins/Slimefun", folder);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    private void registerListeners() {
        new SlimefunBootsListener(this);
        new SlimefunItemListener(this);
        new SlimefunItemConsumeListener(this);
        new BlockPhysicsListener(this);
        new CargoNodeListener(this);
        new MultiBlockListener(this);
        new GadgetsListener(this);
        new DispenserListener(this);
        new BlockListener(this);
        new EnhancedFurnaceListener(this);
        new ItemPickupListener(this);
        new DeathpointListener(this);
        new ExplosionsListener(this);
        new DebugFishListener(this);
        new VanillaMachinesListener(this);
        new FireworksListener(this);
        new WitherListener(this);
        new IronGolemListener(this);
        new EntityInteractionListener(this);
        new MobDropListener(this);
        new VillagerTradingListener(this);
        new ElytraCrashListener(this);

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_15)) {
            new BeeListener(this);
        }

        if (minecraftVersion.isAtLeast(MinecraftVersion.MINECRAFT_1_16)) {
            new PiglinListener(this);
        }

        new ProtectionChecker(this);

        // Item-specific Listeners
        new VampireBladeListener(this, (VampireBlade) SlimefunItems.BLADE_OF_VAMPIRES.getItem());
        new CoolerListener(this, (Cooler) SlimefunItems.COOLER.getItem());
        new SeismicAxeListener(this, (SeismicAxe) SlimefunItems.SEISMIC_AXE.getItem());
        new AncientAltarListener(this, (AncientAltar) SlimefunItems.ANCIENT_ALTAR.getItem(), (AncientPedestal) SlimefunItems.ANCIENT_PEDESTAL.getItem());
        grapplingHookListener.register(this, (GrapplingHook) SlimefunItems.GRAPPLING_HOOK.getItem());
        bowListener.register(this);

        // Toggleable Listeners for performance reasons
        if (config.getBoolean("items.talismans")) {
            new TalismanListener(this);
        }

        if (config.getBoolean("items.soulbound")) {
            new SoulboundListener(this);
        }

        if (config.getBoolean("items.backpacks")) {
            backpackListener.register(this);
        }

        // Handle Slimefun Guide being given on Join
        new SlimefunGuideListener(this, config.getBoolean("guide.receive-on-first-join"));

        // Load/Unload Worlds in Slimefun
        new WorldListener(this);

        // Clear the Slimefun Guide History upon Player Leaving
        new PlayerProfileListener(this);
    }

    private void loadTags() {
        for (SlimefunTag tag : SlimefunTag.valuesCache) {
            try {
                tag.reload();
            } catch (TagMisconfigurationException e) {
                getLogger().log(Level.SEVERE, e, () -> "Failed to load Tag: " + tag.name());
            }
        }
    }

    private void loadItems() {
        try {
            SlimefunItemSetup.setup(this);
        } catch (Exception | LinkageError x) {
            getLogger().log(Level.SEVERE, x, () -> "An Error occured while initializing SlimefunItems for Slimefun " + getVersion());
        }
    }

    private void loadResearches() {
        try {
            ResearchSetup.setupResearches();
        } catch (Exception | LinkageError x) {
            getLogger().log(Level.SEVERE, x, () -> "An Error occured while initializing Slimefun Researches for Slimefun " + getVersion());
        }
    }

    @Nullable
    public static SlimefunPlugin instance() {
        return instance;
    }

    public static Config getCfg() {
        return instance.config;
    }

    public static Config getResearchCfg() {
        return instance.researches;
    }

    public static Config getItemCfg() {
        return instance.items;
    }

    public static GPSNetwork getGPSNetwork() {
        return instance.gpsNetwork;
    }

    public static TickerTask getTickerTask() {
        return instance.ticker;
    }

    /**
     * This returns the version of Slimefun that is currently installed.
     *
     * @return The currently installed version of Slimefun
     */
    public static String getVersion() {
        return instance.getDescription().getVersion();
    }

    public static ProtectionManager getProtectionManager() {
        return instance.protections;
    }

    /**
     * This returns the {@link LocalizationService} of Slimefun.
     *
     * @return The {@link LocalizationService} of Slimefun
     */
    public static LocalizationService getLocalization() {
        return instance.local;
    }

    public static MinecraftRecipeService getMinecraftRecipeService() {
        return instance.recipeService;
    }

    public static CustomItemDataService getItemDataService() {
        return instance.itemDataService;
    }

    public static CustomTextureService getItemTextureService() {
        return instance.textureService;
    }

    public static PermissionsService getPermissionsService() {
        return instance.permissionsService;
    }

    public static BlockDataService getBlockDataService() {
        return instance.blockDataService;
    }

    public static ThirdPartyPluginService getThirdPartySupportService() {
        return instance.thirdPartySupportService;
    }

    public static PerWorldSettingsService getWorldSettingsService() {
        return instance.worldSettingsService;
    }

    /**
     * SFMetrics 反射到的只有这个方法
     * 所以只能加回来了, 但是它并没有实际作用
     * 替代品请见 {@link NUpdater}
     * <p>
     * This method returns the {@link UpdaterService} of Slimefun.
     * It is used to handle automatic updates.
     *
     * @return The {@link UpdaterService} for Slimefun
     */
    public static UpdaterService getUpdater() {
        return instance.updaterService;
    }

    /**
     * This method returns the {@link GitHubService} of Slimefun.
     * It is used to retrieve data from GitHub repositories.
     *
     * @return The {@link GitHubService} for Slimefun
     */
    public static GitHubService getGitHubService() {
        return instance.gitHubService;
    }

    public static SlimefunRegistry getRegistry() {
        return instance.registry;
    }

    public static NetworkManager getNetworkManager() {
        return instance.networkManager;
    }

    public static GrapplingHookListener getGrapplingHookListener() {
        return instance.grapplingHookListener;
    }

    public static BackpackListener getBackpackListener() {
        return instance.backpackListener;
    }

    public static SlimefunBowListener getBowListener() {
        return instance.bowListener;
    }

    /**
     * This method returns a {@link Set} of every {@link Plugin} that lists Slimefun
     * as a required or optional dependency.
     *
     * We will just assume this to be a list of our addons.
     *
     * @return A {@link Set} of every {@link Plugin} that is dependent on Slimefun
     */
    @Nonnull
    public static Set<Plugin> getInstalledAddons() {
        return Arrays.stream(instance.getServer().getPluginManager().getPlugins()).filter(plugin -> plugin.getDescription().getDepend().contains(instance.getName()) || plugin.getDescription().getSoftDepend().contains(instance.getName())).collect(Collectors.toSet());
    }

    /**
     * The {@link Command} that was added by Slimefun.
     *
     * @return Slimefun's command
     */
    public static SlimefunCommand getCommand() {
        return instance.command;
    }

    public static SlimefunProfiler getProfiler() {
        return instance.profiler;
    }

    /**
     * This returns the currently installed version of Minecraft.
     *
     * @return The current version of Minecraft
     */
    public static MinecraftVersion getMinecraftVersion() {
        return instance.minecraftVersion;
    }

    /**
     * This method returns whether this version of Slimefun was newly installed.
     * It will return true if this {@link Server} uses Slimefun for the very first time.
     *
     * @return Whether this is a new installation of Slimefun
     */
    public static boolean isNewlyInstalled() {
        return instance.isNewlyInstalled;
    }

    public static String getCSCoreLibVersion() {
        return CSCoreLib.getLib().getDescription().getVersion();
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/StarWishsama/Slimefun4/issues";
    }

    @Override
    public File getFile() {
        return super.getFile();
    }

    /**
     * This method returns the {@link MetricsService} of Slimefun.
     * It is used to handle sending metric information to bStats.
     *
     * @return The {@link MetricsService} for Slimefun
     */
    public static MetricsService getMetricsService() {
        return instance.metricsService;
    }

    /**
     * This method schedules a delayed synchronous task for Slimefun.
     * <strong>For Slimefun only, not for addons.</strong>
     * <p>
     * This method should only be invoked by Slimefun itself.
     * Addons must schedule their own tasks using their own {@link Plugin} instance.
     *
     * @param runnable The {@link Runnable} to run
     * @param delay    The delay for this task
     * @return The resulting {@link BukkitTask} or null if Slimefun was disabled
     */
    @Nullable
    public static BukkitTask runSync(@Nonnull Runnable runnable, long delay) {
        Validate.notNull(runnable, "Cannot run null");
        Validate.isTrue(delay >= 0, "The delay cannot be negative");

        if (getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            runnable.run();
            return null;
        }

        if (instance == null || !instance.isEnabled()) {
            return null;
        }

        return instance.getServer().getScheduler().runTaskLater(instance, runnable, delay);
    }

    /**
     * This method schedules a synchronous task for Slimefun.
     * <strong>For Slimefun only, not for addons.</strong>
     * <p>
     * This method should only be invoked by Slimefun itself.
     * Addons must schedule their own tasks using their own {@link Plugin} instance.
     *
     * @param runnable The {@link Runnable} to run
     * @return The resulting {@link BukkitTask} or null if Slimefun was disabled
     */
    @Nullable
    public static BukkitTask runSync(@Nonnull Runnable runnable) {
        Validate.notNull(runnable, "Cannot run null");

        if (getMinecraftVersion() == MinecraftVersion.UNIT_TEST) {
            runnable.run();
            return null;
        }

        if (instance == null || !instance.isEnabled()) {
            return null;
        }

        return instance.getServer().getScheduler().runTask(instance, runnable);
    }
}