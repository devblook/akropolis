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

package me.zetastormy.akropolis.module.modules.player;

import java.util.List;
import java.util.UUID;

import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.config.type.Settings;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import net.kyori.adventure.text.Component;

public class PlayerMount extends Module implements LifeCycle {
    private List<String> actions;
    private long cooldownDelay;

    public PlayerMount(AkropolisPlugin plugin) {
        super(plugin, ModuleType.PLAYER_MOUNT);
    }

    @Override
    public void onEnable() {
        Settings.PlayerMount config = getConfig(Settings.class).playerMount();
        actions = config.actions();
        cooldownDelay = config.cooldown();
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();

        if (!(clickedEntity instanceof Player)) {
            return;
        }

        if (player.hasPermission(Permissions.EVENT_PLAYER_MOUNT.getPermission())) {
            UUID uuid = player.getUniqueId();

            if (!tryCooldown(uuid, "player_mount", cooldownDelay)) {
                final Messages messages = this.getPlugin().getConfigManager().getFile(Messages.class).getConfig();
                MessagingUtil.sendWithReplacement(
                        messages.playerMount().cooldownActive(),
                        player,
                        "time",
                        Component.text(getCooldown(uuid, "player_mount"))
                );
                return;
            }

            if (clickedEntity.addPassenger(player)) {
                executeActions(player, actions);
            }
        }
    }
}
