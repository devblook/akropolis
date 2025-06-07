/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2023 DevBlook Team and others
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.cooldown.CooldownManager;
import team.devblook.akropolis.cooldown.CooldownType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Module implements Listener {
    private final AkropolisPlugin plugin;
    private final ModuleType moduleType;
    private List<String> disabledWorlds;
    private final CooldownManager cooldownManager;
    private boolean disabledWorldsInvert = false; // valor por defecto

    protected Module(AkropolisPlugin plugin, ModuleType type) {
        this.plugin = plugin;
        this.moduleType = type;
        this.cooldownManager = plugin.getCooldownManager();
        this.disabledWorlds = new ArrayList<>();
    }

    public void setDisabledWorlds(List<String> disabledWorlds, boolean invert) {
        this.disabledWorldsInvert = invert;

        if (!invert) {
            this.disabledWorlds = disabledWorlds;
            return;
        }

        List<String> inverted = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            if (!disabledWorlds.contains(world.getName())) {
                inverted.add(world.getName());
            }
        }

        this.disabledWorlds = inverted;
    }

    public AkropolisPlugin getPlugin() {
        return plugin;
    }

    public boolean inDisabledWorld(Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        return inDisabledWorld(world);
    }

    public boolean inDisabledWorld(World world) {
        String worldName = world.getName();
        return disabledWorldsInvert
                ? !disabledWorlds.contains(worldName)
                : disabledWorlds.contains(worldName);
    }

    public boolean tryCooldown(UUID uuid, CooldownType type, long delay) {
        return cooldownManager.tryCooldown(uuid, type, delay);
    }

    public long getCooldown(UUID uuid, CooldownType type) {
        return (cooldownManager.getCooldown(uuid, type) / 1000);
    }

    public FileConfiguration getConfig(ConfigType type) {
        return getPlugin().getConfigManager().getFile(type).get();
    }

    public void executeActions(Player player, List<String> actions) {
        getPlugin().getActionManager().executeActions(player, actions);
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public abstract void onEnable();

    public abstract void onDisable();
}
