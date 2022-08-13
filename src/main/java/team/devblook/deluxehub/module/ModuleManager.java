package team.devblook.deluxehub.module;

import com.cryptomorin.xseries.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import team.devblook.deluxehub.DeluxeHubPlugin;
import team.devblook.deluxehub.config.ConfigType;
import team.devblook.deluxehub.module.modules.chat.*;
import team.devblook.deluxehub.module.modules.hologram.HologramManager;
import team.devblook.deluxehub.module.modules.hotbar.HotbarManager;
import team.devblook.deluxehub.module.modules.player.DoubleJump;
import team.devblook.deluxehub.module.modules.player.PlayerListener;
import team.devblook.deluxehub.module.modules.player.PlayerOffHandSwap;
import team.devblook.deluxehub.module.modules.player.PlayerVanish;
import team.devblook.deluxehub.module.modules.visual.scoreboard.ScoreboardManager;
import team.devblook.deluxehub.module.modules.visual.tablist.TablistManager;
import team.devblook.deluxehub.module.modules.world.AntiWorldDownloader;
import team.devblook.deluxehub.module.modules.world.Launchpad;
import team.devblook.deluxehub.module.modules.world.LobbySpawn;
import team.devblook.deluxehub.module.modules.world.WorldProtect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ModuleManager {
    private final Map<ModuleType, Module> modules = new EnumMap<>(ModuleType.class);
    private DeluxeHubPlugin plugin;
    private List<String> disabledWorlds;

    public void loadModules(DeluxeHubPlugin plugin) {
        this.plugin = plugin;

        if (!modules.isEmpty())
            unloadModules();

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).get();
        disabledWorlds = config.getStringList("disabled-worlds.worlds");

        if (config.getBoolean("disabled-worlds.invert")) {
            List<String> newDisabledWorlds = new ArrayList<>();

            for (World world : Bukkit.getWorlds()) {
                newDisabledWorlds.add(world.getName());
            }

            disabledWorlds = newDisabledWorlds;

            for (String world : config.getStringList("disabled-worlds.worlds")) {
                disabledWorlds.remove(world);
            }
        }

        registerModule(new AntiWorldDownloader(plugin), "anti_wdl.enabled");
        registerModule(new DoubleJump(plugin), "double_jump.enabled");
        registerModule(new Launchpad(plugin), "launchpad.enabled");
        registerModule(new ScoreboardManager(plugin), "scoreboard.enabled");
        registerModule(new TablistManager(plugin), "tablist.enabled");
        registerModule(new AutoBroadcast(plugin), "announcements.enabled");
        registerModule(new AntiSwear(plugin), "anti_swear.enabled");
        registerModule(new ChatCommandBlock(plugin), "command_block.enabled");
        registerModule(new ChatLock(plugin));
        registerModule(new CustomCommands(plugin));
        registerModule(new PlayerListener(plugin));
        registerModule(new HotbarManager(plugin));
        registerModule(new WorldProtect(plugin));
        registerModule(new LobbySpawn(plugin));
        registerModule(new PlayerVanish(plugin));
        registerModule(new HologramManager(plugin));

        // Requires 1.9+
        if (ReflectionUtils.supports(9)) {
            registerModule(new PlayerOffHandSwap(plugin), "world_settings.disable_off_hand_swap");
        }

        for (Module module : modules.values()) {
            try {
                module.setDisabledWorlds(disabledWorlds);
                module.onEnable();
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("There was an error loading the " + module.getModuleType() + " module");
                plugin.getLogger().severe("The plugin will now disable..");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                break;
            }
        }

        plugin.getLogger().log(Level.INFO, "Loaded {0} plugin modules.", modules.size());
    }

    public void unloadModules() {
        for (Module module : modules.values()) {
            try {
                HandlerList.unregisterAll(module);
                module.onDisable();
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger()
                        .severe("There was an error unloading the " + module.getModuleType().toString() + " module.");
            }
        }

        modules.clear();
    }

    public Module getModule(ModuleType type) {
        return modules.get(type);
    }

    public void registerModule(Module module) {
        registerModule(module, null);
    }

    public void registerModule(Module module, String isEnabledPath) {
        if (isEnabledPath != null
                && !plugin.getConfigManager().getFile(ConfigType.SETTINGS).get().getBoolean(isEnabledPath, false))
            return;

        plugin.getServer().getPluginManager().registerEvents(module, plugin);
        modules.put(module.getModuleType(), module);
    }

    public boolean isEnabled(ModuleType type) {
        return modules.containsKey(type);
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }
}
