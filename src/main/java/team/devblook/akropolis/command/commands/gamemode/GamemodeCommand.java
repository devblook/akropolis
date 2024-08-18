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

import net.kyori.adventure.text.Component;
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

public class GamemodeCommand extends InjectableCommand {

    public GamemodeCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "gamemode", "Allows you to change gamemode", "/gamemode <gamemode> [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            GameMode gamemode = getGamemode(args[0]);

            if (gamemode == null) {
                Message.GAMEMODE_INVALID.sendFromWithReplacement(sender, "gamemode", TextUtil.parse(args[0]));
                return;
            }

            Message.GAMEMODE_CHANGE.sendFromWithReplacement(player, "gamemode", TextUtil.parse(gamemode.toString().toUpperCase()));
            player.setGameMode(gamemode);

        } else if (args.length == 2) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
                Message.INVALID_PLAYER.sendFromWithReplacement(sender, "player", TextUtil.parse(args[0]));
                return;
            }

            GameMode gamemode = getGamemode(args[0]);

            if (gamemode == null) {
                Message.GAMEMODE_INVALID.sendFromWithReplacement(sender, "gamemode", TextUtil.parse(args[0]));
                return;
            }

            Component gamemodeChange = TextUtil.replace(Message.GAMEMODE_CHANGE.toComponent(), "gamemode", TextUtil.parse(gamemode.toString().toUpperCase()));

            if (sender.getName().equals(player.getName())) {
                player.sendMessage(gamemodeChange);
            } else {
                player.sendMessage(gamemodeChange);
                sender.sendMessage(TextUtil.replace(TextUtil.replace(Message.GAMEMODE_CHANGE_OTHER.toComponent(), "player", player.name()), "gamemode", TextUtil.parse(gamemode.toString().toUpperCase())));
            }

            player.setGameMode(gamemode);
        }

    }

    private GameMode getGamemode(String gamemode) {
        if (gamemode.equals("0") || gamemode.equalsIgnoreCase("survival") || gamemode.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        } else if (gamemode.equals("1") || gamemode.equalsIgnoreCase("creative") || gamemode.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        } else if (gamemode.equals("2") || gamemode.equalsIgnoreCase("adventure") || gamemode.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        } else if (gamemode.equals("3") || gamemode.equalsIgnoreCase("spectator") || gamemode.equalsIgnoreCase("sp")) {
            return GameMode.SPECTATOR;
        }

        return null;
    }
}
