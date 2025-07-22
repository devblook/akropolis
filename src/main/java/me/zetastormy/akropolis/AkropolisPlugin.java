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

package me.zetastormy.akropolis;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.zetastormy.akropolis.action.ActionManager;
import me.zetastormy.akropolis.command.CommandManager;
import me.zetastormy.akropolis.config.ConfigManager;
import me.zetastormy.akropolis.cooldown.CooldownManager;
import me.zetastormy.akropolis.hook.HooksManager;
import me.zetastormy.akropolis.inventory.InventoryManager;
import me.zetastormy.akropolis.module.ModuleManager;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.hologram.HologramManager;
import me.zetastormy.akropolis.module.modules.player.FightModeManager;
import me.zetastormy.akropolis.module.modules.world.SongPlayerManager;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;

public class AkropolisPlugin extends JavaPlugin {
    private static AkropolisPlugin plugin;
    private ConfigManager configManager;
    private ActionManager actionManager;
    private HooksManager hooksManager;
    private CommandManager commandManager;
    private CooldownManager cooldownManager;
    private ModuleManager moduleManager;
    private InventoryManager inventoryManager;
    private ScoreboardLibrary scoreboardLibrary;

    @Override
    public void onEnable() {
        // Set the unique plugin instance
        setInstance(this);

        long start = System.currentTimeMillis();

        getLogger().log(Level.INFO, "     _    _                          _ _     ");
        getLogger().log(Level.INFO, "    / \\  | | ___ __ ___  _ __   ___ | (_)___ ");
        getLogger().log(Level.INFO, "   / _ \\ | |/ / '__/ _ \\| '_ \\ / _ \\| | / __|");
        getLogger().log(Level.INFO, "  / ___ \\|   <| | | (_) | |_) | (_) | | \\__ \\");
        getLogger().log(Level.INFO, " /_/   \\_\\_|\\_\\_|  \\___/| .__/ \\___/|_|_|___/");
        getLogger().log(Level.INFO, "                        |_|                  ");
        getLogger().log(Level.INFO, "Author: ZetaStormy");
        getLogger().log(Level.INFO, "Based on DeluxeHub by ItsLewizzz.");
        getLogger().log(Level.INFO, "--------");

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

        //Scoreboard library
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
        }

        // Core plugin modules
        moduleManager = new ModuleManager();
        moduleManager.loadModules(this);

        // Inventory (GUI) manager
        inventoryManager = new InventoryManager();
        if (!hooksManager.isHookEnabled("HEAD_DATABASE")) inventoryManager.onEnable(this);

        // Action system
        actionManager = new ActionManager(this);

        // Register BungeeCord channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getLogger().log(Level.INFO, "Successfully loaded in {0}ms.", (System.currentTimeMillis() - start));
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        if (moduleManager != null) moduleManager.unloadModules();

        if (scoreboardLibrary != null) scoreboardLibrary.close();

        if (inventoryManager != null) inventoryManager.onDisable();

        if (configManager != null) configManager.saveData();
    }

    public void reload() {
        Bukkit.getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        configManager.reloadFiles();

        inventoryManager.onDisable();
        inventoryManager.onEnable(this);

        scoreboardLibrary.close();

        try {
            commandManager.reload();
            ((CraftServer) getServer()).syncCommands();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
        } catch (NoPacketAdapterAvailableException e) {
            scoreboardLibrary = new NoopScoreboardLibrary();
        }

        moduleManager.loadModules(this);
    }

    public static synchronized void setInstance(AkropolisPlugin instance) {
        if (plugin == null) {
            plugin = instance;
        }
    }

    public static synchronized AkropolisPlugin getInstance() {
        return plugin;
    }

    public FightModeManager getFightModeManager() {
        return (FightModeManager) moduleManager.getModule(ModuleType.FIGHT_MODE);
    }

    public SongPlayerManager getSongPlayerManager() {
        return (SongPlayerManager) moduleManager.getModule(ModuleType.SONG_PLAYER);
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

    public ScoreboardLibrary getScoreboardLibrary() {
        return scoreboardLibrary;
    }
}
