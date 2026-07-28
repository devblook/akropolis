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

package me.zetastormy.akropolis.module.modules.world;

import me.zetastormy.akropolis.config.type.Data;
import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.config.type.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.slf4j.Logger;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.AkroLocation;
import me.zetastormy.akropolis.util.MessagingUtil;
import net.kyori.adventure.text.Component;

public class LobbySpawn extends Module implements LifeCycle {
    private boolean spawnJoin;
    private AkroLocation location = null;
    private final Logger logger = this.getPlugin().getSLF4JLogger();

    public LobbySpawn(AkropolisPlugin plugin) {
        super(plugin, ModuleType.LOBBY);
    }

    @Override
    public void onEnable() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), () -> {
            final Data dataConfig = getConfig(Data.class);
            if (dataConfig.getSpawn() != null)
                this.location = dataConfig.getSpawn();
        });

        this.spawnJoin = getConfig(Settings.class).joinSettings().spawnJoin();
    }

    @Override
    public void onDisable() {
        getConfig(Data.class).setSpawn(this.location);
    }

    public AkroLocation getLocation() {
        return location;
    }

    public void setLocation(final org.bukkit.Location bukkitLocation) {
        this.setLocation(AkroLocation.fromBukkitLocation(bukkitLocation));
    }

    public void setLocation(AkroLocation location) {
        this.location = location;
        this.getConfigFile(Data.class).save(this.getConfigurationExecutorService());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (spawnJoin) {
            if (location == null) {
                this.logger.error(
                    "Couldn't teleport player '{}' ({}) to the lobby on join because it's unset",
                    player.getName(),
                    player.getUniqueId()
                );
                MessagingUtil.send(
                    this.getConfig(Messages.class).lobby().teleportLobbyUnset(),
                    player
                );
                return;
            }

            org.bukkit.Location bukkitLoc = location.toBukkitLocation();
            if (bukkitLoc == null) {
                this.logger.error(
                    "Couldn't teleport player '{}' ({}) to the lobby on join because world '{}' is not loaded",
                    player.getName(),
                    player.getUniqueId(),
                    location.worldKey().asString()
                );
                MessagingUtil.sendWithReplacement(
                    this.getConfig(Messages.class).lobby().teleportWorldUnloaded(),
                    player,
                    "world",
                    Component.text(location.worldKey().asString())
                );
                return;
            }
            player.teleport(bukkitLoc);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!inDisabledWorld(player.getLocation())) {
            if (location == null) {
                this.logger.error(
                    "Couldn't respawn player '{}' ({}) in the lobby because it's unset",
                    player.getName(),
                    player.getUniqueId()
                );
                MessagingUtil.send(
                    this.getConfig(Messages.class).lobby().teleportLobbyUnset(),
                    player
                );
                return;
            }

            org.bukkit.Location bukkitLoc = location.toBukkitLocation();
            if (bukkitLoc == null) {
                this.logger.error(
                    "Couldn't respawn player '{}' ({}) in the lobby because world '{}' is not loaded",
                    player.getName(),
                    player.getUniqueId(),
                    location.worldKey().asString()
                );
                MessagingUtil.sendWithReplacement(
                    this.getConfig(Messages.class).lobby().teleportWorldUnloaded(),
                    player,
                    "world",
                    Component.text(location.worldKey().asString())
                );
                return;
            }
            event.setRespawnLocation(bukkitLoc);
        }
    }
}
