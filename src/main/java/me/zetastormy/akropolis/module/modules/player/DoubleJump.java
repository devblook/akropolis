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

package me.zetastormy.akropolis.module.modules.player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.List;
import java.util.UUID;

public class DoubleJump extends Module {
    private long cooldownDelay;
    private double launch;
    private double launchY;
    private List<String> actions;

    public DoubleJump(AkropolisPlugin plugin) {
        super(plugin, ModuleType.DOUBLE_JUMP);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        cooldownDelay = config.getLong("double_jump.cooldown", 0);
        launch = config.getDouble("double_jump.launch_power", 1.3);
        launchY = config.getDouble("double_jump.launch_power_y", 1.2);
        actions = config.getStringList("double_jump.actions");

        if (launch > 4.0)
            launch = 4.0;
        if (launchY > 4.0)
            launchY = 4.0;
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        // Perform checks
        if (player.hasPermission(Permissions.DOUBLE_JUMP_BYPASS.getPermission()) || player.hasPermission(Permissions.COMMAND_FLIGHT.getPermission()))
            return;
        else if (inDisabledWorld(player.getLocation()))
            return;
        else if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
            return;
        else if (!event.isFlying())
            return;
        else if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 2, 0)).getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }

        // All pre-checks passed, now handle double jump
        event.setCancelled(true);

        // Check for cooldown
        UUID uuid = player.getUniqueId();

        if (!tryCooldown(uuid, "double_jump", cooldownDelay)) {
            Message.DOUBLE_JUMP_COOLDOWN.sendFromWithReplacement(player, "time", Component.text(getCooldown(uuid, "double_jump")));
            return;
        }

        // Execute double jump
        player.setVelocity(player.getLocation().getDirection().multiply(launch).setY(launchY));
        executeActions(player, actions);
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
            player.setAllowFlight(!inDisabledWorld(player.getLocation()));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR)
            player.setAllowFlight(!inDisabledWorld(player.getLocation()));
    }

    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation())) return;

        GameMode newGameMode = event.getNewGameMode();
        if (newGameMode != GameMode.CREATIVE && newGameMode != GameMode.SPECTATOR) {
            Bukkit.getScheduler().runTaskLater(getPlugin(), () -> player.setAllowFlight(true), 1L);
        }
    }
}
