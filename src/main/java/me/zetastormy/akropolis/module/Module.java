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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import me.zetastormy.akropolis.config.ConfigurationContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.cooldown.CooldownManager;
import org.jetbrains.annotations.NotNull;

public class Module implements Listener {
    private final AkropolisPlugin plugin;
    private final ModuleType moduleType;
    private List<String> disabledWorlds;
    private final CooldownManager cooldownManager;

    protected Module(AkropolisPlugin plugin, ModuleType type) {
        this.plugin = plugin;
        this.moduleType = type;
        this.cooldownManager = plugin.getCooldownManager();
        this.disabledWorlds = new ArrayList<>();
    }

    public void setDisabledWorlds(List<String> disabledWorlds) {
        this.disabledWorlds = disabledWorlds;
    }

    public AkropolisPlugin getPlugin() {
        return plugin;
    }

    public boolean inDisabledWorld(Location location) {
        World world = location.getWorld();

        if (world == null) return false;

        return disabledWorlds.contains(world.getName());
    }

    public boolean inDisabledWorld(World world) {
        return disabledWorlds.contains(world.getName());
    }

    public boolean tryCooldown(UUID uuid, String type, long delay) {
        return cooldownManager.tryCooldown(uuid, type, delay);
    }

    public long getCooldown(UUID uuid, String type) {
        return (cooldownManager.getCooldown(uuid, type) / 1000);
    }

    public <T> T getConfig(Class<T> type) {
        return getPlugin().getConfigManager().getFile(type).getConfig();
    }

    public <T> ConfigurationContainer<T> getConfigFile(Class<T> type) {
        return getPlugin().getConfigManager().getFile(type);
    }

    public @NotNull ExecutorService getConfigurationExecutorService() {
        return this.getPlugin().getConfigManager().getConfigurationExecutorService();
    }

    public void executeActions(Player player, List<String> actions) {
        getPlugin().getActionManager().executeActions(player, actions);
    }

    public ModuleType getModuleType() {
        return moduleType;
    }
}
