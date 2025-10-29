/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2025 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package me.zetastormy.akropolis.module;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.module.modules.chat.AntiSwear;
import me.zetastormy.akropolis.module.modules.chat.AutoBroadcast;
import me.zetastormy.akropolis.module.modules.chat.ChatCommandBlock;
import me.zetastormy.akropolis.module.modules.chat.ChatLock;
import me.zetastormy.akropolis.module.modules.chat.groups.ChatGroups;
import me.zetastormy.akropolis.module.modules.hologram.HologramManager;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarManager;
import me.zetastormy.akropolis.module.modules.player.DoubleJump;
import me.zetastormy.akropolis.module.modules.player.FightModeManager;
import me.zetastormy.akropolis.module.modules.player.PlayerListener;
import me.zetastormy.akropolis.module.modules.player.PlayerMount;
import me.zetastormy.akropolis.module.modules.player.PlayerOffHandSwap;
import me.zetastormy.akropolis.module.modules.player.PlayerVanish;
import me.zetastormy.akropolis.module.modules.visual.bossbar.BossBarBroadcast;
import me.zetastormy.akropolis.module.modules.visual.nametag.NametagManager;
import me.zetastormy.akropolis.module.modules.visual.scoreboard.ScoreboardManager;
import me.zetastormy.akropolis.module.modules.visual.tablist.TablistManager;
import me.zetastormy.akropolis.module.modules.world.AntiWorldDownloader;
import me.zetastormy.akropolis.module.modules.world.Launchpad;
import me.zetastormy.akropolis.module.modules.world.LobbySpawn;
import me.zetastormy.akropolis.module.modules.world.SongPlayerManager;
import me.zetastormy.akropolis.module.modules.world.WorldProtect;

public class ModuleManager implements Listener {
    private final Map<ModuleType, Module> modules = new EnumMap<>(ModuleType.class);
    private AkropolisPlugin plugin;
    private List<String> disabledWorlds;
    private List<String> configuredWorldsList;
    private boolean invertWorldList;

    public void loadModules(AkropolisPlugin plugin) {
        this.plugin = plugin;

        if (!modules.isEmpty())
            unloadModules();

        FileConfiguration config = plugin.getConfigManager().getFile(ConfigType.SETTINGS).get();
        configuredWorldsList = new ArrayList<>(config.getStringList("disabled-worlds.worlds"));
        invertWorldList = config.getBoolean("disabled-worlds.invert");
        
        // Recalculate disabled worlds based on current loaded worlds
        updateDisabledWorldsList();
        
        // Register this listener to handle dynamic world loading
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // Log enabled and disabled worlds
        List<String> enabledWorlds = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (!disabledWorlds.contains(world.getName())) {
                enabledWorlds.add(world.getName());
            }
        }

        registerModule(new AntiWorldDownloader(plugin), "anti_wdl.enabled");
        registerModule(new DoubleJump(plugin), "double_jump.enabled");
        registerModule(new PlayerMount(plugin), "player_mount.enabled");
        registerModule(new Launchpad(plugin), "launchpad.enabled");
        registerModule(new BossBarBroadcast(plugin), "boss_bar_announcements.enabled");
        registerModule(new NametagManager(plugin), "nametag.enabled");
        registerModule(new ScoreboardManager(plugin), "scoreboard.enabled");
        registerModule(new TablistManager(plugin), "tablist.enabled");
        registerModule(new AutoBroadcast(plugin), "announcements.enabled");
        registerModule(new AntiSwear(plugin), "anti_swear.enabled");
        registerModule(new ChatCommandBlock(plugin), "command_block.enabled");
        registerModule(new ChatGroups(plugin), "groups.enabled");
        registerModule(new ChatLock(plugin));
        registerModule(new PlayerListener(plugin));
        registerModule(new HotbarManager(plugin));
        registerModule(new WorldProtect(plugin));
        registerModule(new LobbySpawn(plugin));
        registerModule(new PlayerVanish(plugin));
        registerModule(new HologramManager(plugin));
        registerModule(new PlayerOffHandSwap(plugin), "world_settings.disable_off_hand_swap");
        registerModule(new FightModeManager(plugin), "fight_mode.enabled");

        if (plugin.getHookManager().isHookEnabled("NOTEBLOCK_API"))
            registerModule(new SongPlayerManager(plugin), "song_player.enabled");

        for (Module module : modules.values()) {
            try {
                module.setDisabledWorlds(disabledWorlds);

                if (module instanceof LifeCycle) {
                    ((LifeCycle) module).onEnable();
                }
            } catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().severe("There was an error loading the " + module.getModuleType() + " module");
                plugin.getLogger().severe("The plugin will now disable...");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                break;
            }
        }

        plugin.getLogger().log(Level.INFO, "Loaded {0} plugin modules.", modules.size());
    }

    private void updateDisabledWorldsList() {
        List<String> newDisabledWorlds = new ArrayList<>();

        if (invertWorldList) {
            // Invert mode: whitelist (configured worlds are ENABLED, others are DISABLED)
            for (World world : Bukkit.getWorlds()) {
                String worldName = world.getName();
                if (!configuredWorldsList.contains(worldName)) {
                    newDisabledWorlds.add(worldName);
                }
            }
        } else {
            // Normal mode: blacklist (configured worlds are DISABLED)
            newDisabledWorlds.addAll(configuredWorldsList);
        }

        disabledWorlds = newDisabledWorlds;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        String worldName = event.getWorld().getName();
        
        if (invertWorldList) {
            // In invert mode, new worlds should be disabled unless in config
            if (!configuredWorldsList.contains(worldName)) {
                disabledWorlds.add(worldName);
            } else {
                // Remove from disabled if it wasn't already (just to be safe)
                disabledWorlds.remove(worldName);
            }
        } else {
            // In normal mode, remove from disabled if it's not explicitly configured
            if (!configuredWorldsList.contains(worldName)) {
                disabledWorlds.remove(worldName);
            }
        }
        
        // Update all modules with the new disabled worlds list
        for (Module module : modules.values()) {
            module.setDisabledWorlds(disabledWorlds);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        String worldName = event.getWorld().getName();
        disabledWorlds.remove(worldName);
    }

    public void unloadModules() {
        // Unregister this listener
        HandlerList.unregisterAll((Listener) this);
        
        for (Module module : modules.values()) {
            try {
                HandlerList.unregisterAll(module);

                if (module instanceof LifeCycle) {
                    ((LifeCycle) module).onDisable();
                }
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
