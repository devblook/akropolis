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

package team.devblook.akropolis.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.player.PlayerVanish;

import java.util.List;

public class VanishCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public VanishCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "vanish", "Disappear into thin air!", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_VANISH.getPermission())) {
            Message.NO_PERMISSION.sendFrom(sender);
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Console cannot set the spawn location.");
            return;
        }

        PlayerVanish vanishModule = ((PlayerVanish) plugin.getModuleManager().getModule(ModuleType.VANISH));
        vanishModule.toggleVanish(player);

    }
}
