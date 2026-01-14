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

package me.zetastormy.akropolis.command.commands.gamemode;

import java.util.List;

import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.util.text.TextUtil;
import net.kyori.adventure.text.Component;

public class AdventureCommand extends InjectableCommand {

    public AdventureCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "gma", "Change to adventure mode", "/gma [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        final Messages messages = this.getPlugin().getConfigManager().getFile(Messages.class).getConfig();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessagingUtil.send(messages.general().consoleNotAllowed(), sender);
                return;
            }

            if (!player.hasPermission(Permissions.COMMAND_GAMEMODE.getPermission())) {
                MessagingUtil.send(messages.general().noPermission(), sender);
                return;
            }

            MessagingUtil.sendWithReplacement(
                    messages.gamemode().gamemodeChange(),
                    player,
                    "gamemode",
                    Component.text("ADVENTURE")
            );
            player.setGameMode(GameMode.ADVENTURE);
        } else if (args.length == 1) {
            if (!sender.hasPermission(Permissions.COMMAND_GAMEMODE_OTHERS.getPermission())) {
                MessagingUtil.send(messages.general().noPermission(), sender);
                return;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessagingUtil.sendWithReplacement(
                        messages.general().invalidPlayer(),
                        sender,
                        "player",
                        Component.text(args[0])
                );
                return;
            }

            final Component gamemodeChangeMessage = TextUtil.replace(
                    MessagingUtil.toComponent(messages.gamemode().gamemodeChange()),
                    "gamemode",
                    Component.text("ADVENTURE")
            );

            if (sender.getName().equals(target.getName())) {
                MessagingUtil.send(gamemodeChangeMessage, sender);
            } else {
                MessagingUtil.send(gamemodeChangeMessage, target);
                MessagingUtil.sendWithReplacement(
                        messages.gamemode().gamemodeChangeOther(),
                        sender,
                        "gamemode", Component.text("ADVENTURE"),
                        "player", target.name()
                );
            }

            target.setGameMode(GameMode.ADVENTURE);
        }

    }
}
