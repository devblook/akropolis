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

package team.devblook.akropolis.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.Message;

import java.util.List;

public class CustomCommand extends InjectableCommand {
    private String permission;
    private final List<String> actions;

    public CustomCommand(Plugin plugin, String name, List<String> aliases, List<String> actions) {
        super(plugin, name, "A custom Akropolis command", aliases);
        this.actions = actions;
    }

    @Override
    protected void onCommand(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Message.CONSOLE_NOT_ALLOWED.sendFrom(sender);
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            Message.CUSTOM_COMMAND_NO_PERMISSION.sendFrom(sender);
            return;
        }

        AkropolisPlugin.getInstance().getActionManager().executeActions(player, actions);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getActions() {
        return actions;
    }
}
