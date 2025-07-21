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

package me.zetastormy.akropolis.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.config.Message;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.world.LobbySpawn;
import me.zetastormy.akropolis.util.text.TextUtil;

public class SetLobbyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public SetLobbyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "setlobby", "Set the lobby location", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_SET_LOBBY.getPermission())) {
            Message.NO_PERMISSION.send(sender);
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return;
        }

        if (plugin.getModuleManager().getDisabledWorlds().contains(player.getWorld().getName())) {
            sender.sendMessage(TextUtil.parse("<red>You cannot set the lobby location in a disabled world."));
            return;
        }

        LobbySpawn lobbyModule = ((LobbySpawn) plugin.getModuleManager().getModule(ModuleType.LOBBY));
        lobbyModule.setLocation(player.getLocation());
        Message.SET_LOBBY.send(sender);

    }
}
