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

package team.devblook.akropolis.command.commands.gamemode;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class CreativeCommand extends InjectableCommand {

    public CreativeCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "gmc", "Change to creative mode", "/gmc [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                Message.NO_PERMISSION.sendFrom(player);
                return;
            }

            Message.GAMEMODE_CHANGE.sendFromWithReplacement(player, "gamemode", TextUtil.parse("CREATIVE"));
            player.setGameMode(GameMode.CREATIVE);
        } else if (args.length == 1) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                Message.INVALID_PLAYER.sendFromWithReplacement(sender, "player", TextUtil.parse(args[0]));
                return;
            }

            if (sender.getName().equals(player.getName())) {
                Message.GAMEMODE_CHANGE.sendFromWithReplacement(player, "gamemode", TextUtil.parse("CREATIVE"));
            } else {
                Message.GAMEMODE_CHANGE.sendFromWithReplacement(player, "gamemode", TextUtil.parse("CREATIVE"));
                sender.sendMessage(TextUtil.replace(TextUtil.replace(Message.GAMEMODE_CHANGE_OTHER.toComponent(), "player", player.name()), "gamemode", TextUtil.parse("CREATIVE")));
            }

            player.setGameMode(GameMode.CREATIVE);
        }

    }
}
