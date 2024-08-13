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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.ConfigManager;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class FlyCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;
    private final FileConfiguration dataConfig;
    private final boolean saveState;

    public FlyCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "fly", "Toggle flight mode", "/fly [player]", aliases);
        this.plugin = plugin;

        ConfigManager configManager = plugin.getConfigManager();

        this.dataConfig = configManager.getFile(ConfigType.DATA).get();
        this.saveState = configManager.getFile(ConfigType.SETTINGS).get().getBoolean("fly.save_state", false);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
                return;
            }

            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT.getPermission()))) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            if (player.getAllowFlight()) {
                Message.FLIGHT_DISABLE.sendFrom(player);
                toggleFlight(player, false);
            } else {
                Message.FLIGHT_ENABLE.sendFrom(player);
                toggleFlight(player, true);
            }
        } else if (args.length == 1) {
            if (!(sender.hasPermission(Permissions.COMMAND_FLIGHT_OTHERS.getPermission()))) {
                Message.NO_PERMISSION.sendFrom(sender);
                return;
            }

            Player player = Bukkit.getPlayer(args[0]);

            if (player == null) {
                Message.INVALID_PLAYER.sendFromWithReplacement(sender, "player", TextUtil.parse(args[0]));
                return;
            }

            if (player.getAllowFlight()) {
                Message.FLIGHT_DISABLE.sendFrom(player);
                Message.FLIGHT_DISABLE_OTHER.sendFromWithReplacement(sender, "player", player.name());
                toggleFlight(player, false);
            } else {
                Message.FLIGHT_ENABLE.sendFrom(player);
                Message.FLIGHT_ENABLE_OTHER.sendFromWithReplacement(sender, "player", player.name());
                toggleFlight(player, true);
            }
        }

    }

    private void toggleFlight(Player player, boolean value) {
        player.setAllowFlight(value);
        player.setFlying(value);

        if (!saveState) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                dataConfig.set("players." + player.getUniqueId() + ".fly", value));
    }
}
