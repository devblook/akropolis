/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2022 DevBlook Team and others
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

package team.devblook.akropolis.module.modules.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.command.CustomCommand;
import team.devblook.akropolis.config.Messages;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

import java.util.List;

public class CustomCommands extends Module {
    private List<CustomCommand> commands;

    public CustomCommands(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CUSTOM_COMMANDS);
    }

    @Override
    public void onEnable() {
        commands = getPlugin().getCommandManager().getCustomCommands();
    }

    @Override
    public void onDisable() {
        // TODO: Refactor to follow Liskov Substitution principle.
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (inDisabledWorld(player.getLocation()))
            return;

        String command = event.getMessage().toLowerCase().replace("/", "");

        for (CustomCommand customCommand : commands) {
            if (customCommand.getAliases().stream().anyMatch(alias -> alias.equals(command))) {
                if (customCommand.getPermission() != null && !player.hasPermission(customCommand.getPermission())) {
                    player.sendMessage(Messages.CUSTOM_COMMAND_NO_PERMISSION.toComponent());
                    event.setCancelled(true);
                    return;
                }

                event.setCancelled(true);
                getPlugin().getActionManager().executeActions(player, customCommand.getActions());
            }
        }
    }
}
