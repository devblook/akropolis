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

import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.util.text.TextUtil;
import org.jetbrains.annotations.NotNull;

public class ClearchatCommand extends InjectableCommand {

    public ClearchatCommand(final @NotNull AkropolisPlugin plugin, @NotNull List<String> aliases) {
        super(plugin, "clearchat", "Clear global or a player's chat", "/clearchat [player]", aliases);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        final Messages messages = this.getPlugin().getConfigManager().getFile(Messages.class).getConfig();

        if (!(sender.hasPermission(Permissions.COMMAND_CLEARCHAT.getPermission()))) {
            MessagingUtil.send(messages.general().noPermission(), sender);
            return;
        }

        if (args.length == 0) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (int i = 0; i < 100; i++) {
                    player.sendMessage("");
                }

                MessagingUtil.sendWithReplacement(
                        messages.chat().clearChat(),
                        player,
                        "player", sender.name()
                );
            }
        } else if (args.length == 1) {

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                MessagingUtil.sendWithReplacement(
                        messages.general().invalidPlayer(),
                        sender,
                        "player", TextUtil.parse(args[0])
                );
                return;
            }

            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            MessagingUtil.sendWithReplacement(
                    messages.chat().clearChatPlayer(),
                    sender,
                    "player", sender.name()
            );
        }

    }
}
