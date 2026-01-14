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

import me.zetastormy.akropolis.config.ConfigurationContainer;
import me.zetastormy.akropolis.config.type.Data;
import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.config.type.Settings;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.config.ConfigManager;
import me.zetastormy.akropolis.util.text.TextUtil;

public class FlyCommand extends InjectableCommand {
    private final ConfigurationContainer<Data> dataConfig;
    private final boolean saveState;

    public FlyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "fly", "Toggle flight mode", "/fly [player]", aliases);

        ConfigManager configManager = plugin.getConfigManager();

        this.dataConfig = configManager.getFile(Data.class);
        this.saveState = configManager.getFile(Settings.class).getConfig().fly().saveState();
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        final var plugin = this.getPlugin();
        final Messages messages = plugin.getConfigManager().getFile(Messages.class).getConfig();

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                MessagingUtil.send(messages.general().consoleNotAllowed(), sender);
                return;
            }

            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT.getPermission()))) {
                MessagingUtil.send(messages.general().noPermission(), sender);
                return;
            }

            if (player.getAllowFlight()) {
                MessagingUtil.send(messages.flight().disable(), player);
                toggleFlight(player, false);
            } else {
                MessagingUtil.send(messages.flight().enable(), player);
                toggleFlight(player, true);
            }
        } else if (args.length == 1) {
            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT_OTHERS.getPermission()))) {
                MessagingUtil.send(messages.general().noPermission(), sender);
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessagingUtil.sendWithReplacement(
                        messages.general().invalidPlayer(),
                        sender,
                        "player", TextUtil.parse(args[0])
                );
                return;
            }

            if (target.getAllowFlight()) {
                MessagingUtil.send(messages.flight().disable(), target);

                if (!sender.getName().equals(target.getName())) {
                    MessagingUtil.sendWithReplacement(
                            messages.flight().disableOther(),
                            sender,
                            "player", target.name()
                    );
                }

                toggleFlight(target, false);
            } else {
                MessagingUtil.send(messages.flight().enable(), target);

                if (!sender.getName().equals(target.getName())) {
                    MessagingUtil.sendWithReplacement(
                            messages.flight().enableOther(),
                            sender,
                            "player",
                            target.name()
                    );
                }

                toggleFlight(target, true);
            }
        }

    }

    private void toggleFlight(Player player, boolean value) {
        player.setAllowFlight(value);
        player.setFlying(value);

        if (!saveState) return;

        final var plugin = this.getPlugin();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                dataConfig.getConfig().getPlayers().get(player.getUniqueId()).setFly(value));
    }
}
