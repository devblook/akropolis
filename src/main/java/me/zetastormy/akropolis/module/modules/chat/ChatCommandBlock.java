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

package me.zetastormy.akropolis.module.modules.chat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.config.ConfigType;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;

public class ChatCommandBlock extends Module implements LifeCycle {
    private List<String> blockedCommands;

    public ChatCommandBlock(AkropolisPlugin plugin) {
        super(plugin, ModuleType.COMMAND_BLOCK);
    }

    @Override
    public void onEnable() {
        blockedCommands = getConfig(ConfigType.SETTINGS).getStringList("command_block.blocked_commands");
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation())
                || player.hasPermission(Permissions.BLOCKED_COMMANDS_BYPASS.getPermission()))
            return;

        if (blockedCommands.contains(event.getMessage().toLowerCase())) {
            event.setCancelled(true);
            Message.COMMAND_BLOCKED.send(player);
        }
    }
}
