package team.devblook.deluxehub;

import net.megavex.scoreboardlibrary.ScoreboardLibraryImplementation;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.exception.ScoreboardLibraryLoadException;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import team.devblook.deluxehub.action.ActionManager;
import team.devblook.deluxehub.command.CommandManager;
import team.devblook.deluxehub.config.ConfigManager;
import team.devblook.deluxehub.cooldown.CooldownManager;
import team.devblook.deluxehub.hook.HooksManager;
import team.devblook.deluxehub.inventory.InventoryManager;
import team.devblook.deluxehub.module.ModuleManager;
import team.devblook.deluxehub.module.ModuleType;
import team.devblook.deluxehub.module.modules.hologram.HologramManager;

import java.util.logging.Level;

public class DeluxeHubPlugin extends JavaPlugin {
    private static DeluxeHubPlugin plugin;
    private static final int BSTATS_ID = 3151;
    private ConfigManager configManager;
    private ActionManager actionManager;
    private HooksManager hooksManager;
    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ModuleManager moduleManager;
    private InventoryManager inventoryManager;
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        // Set the unique plugin instance
        setInstance(this);

        long start = System.currentTimeMillis();

        getLogger().log(Level.INFO, " _   _            _          _    _ ");
        getLogger().log(Level.INFO, "| \\ |_ |  | | \\/ |_ |_| | | |_)   _)");
        getLogger().log(Level.INFO, "|_/ |_ |_ |_| /\\ |_ | | |_| |_)   _)");
        getLogger().log(Level.INFO, "");
        getLogger().log(Level.INFO, "Version: {0}", getDescription().getVersion());
        getLogger().log(Level.INFO, "Author: ItsLewizzz");
        getLogger().log(Level.INFO, "");

        // Check if using Paper
        try {
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException ex) {
            getLogger().severe("DeluxeHub requires Paper 1.19+ to run, you can download");
            getLogger().severe("Paper here: https://papermc.io/downloads.");
            getLogger().severe("The plugin will now disable.");
            getPluginLoader().disablePlugin(this);
            return;
        }

        // Enable bStats metrics
        new MetricsLite(this, BSTATS_ID);

        // Check plugin hooks
        hooksManager = new HooksManager(this);

        // Load config files
        configManager = new ConfigManager();
        configManager.loadFiles(this);

        // If there were any configuration errors we should not continue
        if (!getServer().getPluginManager().isPluginEnabled(this)) return;

        // Command manager
        commandManager = new CommandManager(this);
        commandManager.reload();

        // Cooldown manager
        cooldownManager = new CooldownManager();

        // Inventory (GUI) manager
        inventoryManager = new InventoryManager();
        if (!hooksManager.isHookEnabled("HEAD_DATABASE")) inventoryManager.onEnable(this);

        //Scoreboard library
        try {
            ScoreboardLibraryImplementation.init();
        } catch (ScoreboardLibraryLoadException e) {
            e.printStackTrace();
            return;
        }

        scoreboardManager = ScoreboardManager.scoreboardManager(this);

        // Core plugin modules
        moduleManager = new ModuleManager();
        moduleManager.loadModules(this);

        // Action system
        actionManager = new ActionManager(this);

        // Register BungeeCord channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLogger().log(Level.INFO, "Successfully loaded in {0} ms", (System.currentTimeMillis() - start));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        if (moduleManager != null) moduleManager.unloadModules();

        if (scoreboardManager != null) {
            scoreboardManager.close();
            ScoreboardLibraryImplementation.close();
        }

        if (inventoryManager != null) inventoryManager.onDisable();

        if (configManager != null) configManager.saveData();
    }

    public void reload() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        configManager.reloadFiles();

        inventoryManager.onDisable();
        inventoryManager.onEnable(this);

        getCommandManager().reload();

        moduleManager.loadModules(this);
    }

    public static synchronized void setInstance(DeluxeHubPlugin instance) {
        if (plugin == null) {
            plugin = instance;
        }
    }

    public static synchronized DeluxeHubPlugin getInstance() {
        return plugin;
    }


    public HologramManager getHologramManager() {
        return (HologramManager) moduleManager.getModule(ModuleType.HOLOGRAMS);
    }

    public HooksManager getHookManager() {
        return hooksManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
}