/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2024 DevBlook Team and others
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

package team.devblook.akropolis.module;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.modules.chat.AntiSwear;
import team.devblook.akropolis.module.modules.chat.AutoBroadcast;
import team.devblook.akropolis.module.modules.chat.ChatCommandBlock;
import team.devblook.akropolis.module.modules.chat.ChatLock;
import team.devblook.akropolis.module.modules.chat.groups.ChatGroups;
import team.devblook.akropolis.module.modules.hologram.HologramManager;
import team.devblook.akropolis.module.modules.hotbar.HotbarManager;
import team.devblook.akropolis.module.modules.player.DoubleJump;
import team.devblook.akropolis.module.modules.player.PlayerListener;
import team.devblook.akropolis.module.modules.player.PlayerOffHandSwap;
import team.devblook.akropolis.module.modules.player.PlayerVanish;
import team.devblook.akropolis.module.modules.visual.bossbar.BossBarBroadcast;
import team.devblook.akropolis.module.modules.visual.nametag.NametagManager;
import team.devblook.akropolis.module.modules.visual.scoreboard.ScoreboardManager;
import team.devblook.akropolis.module.modules.visual.tablist.TablistManager;
import team.devblook.akropolis.module.modules.world.AntiWorldDownloader;
import team.devblook.akropolis.module.modules.world.Launchpad;
import team.devblook.akropolis.module.modules.world.LobbySpawn;
import team.devblook.akropolis.module.modules.world.WorldProtect;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ModuleManager {
    private final Map<ModuleType, Module> modules = new EnumMap<>(ModuleType.class);
    private AkropolisPlugin plugin;
    private List<String> disabledWorlds;

    public void loadModules(AkropolisPlugin plugin) {
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
