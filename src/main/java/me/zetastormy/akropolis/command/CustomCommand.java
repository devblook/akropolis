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

package me.zetastormy.akropolis.command;

import java.util.List;

import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomCommand extends InjectableCommand {
    private String permission;
    private final @NotNull List<String> actions;

    public CustomCommand(
            final @NotNull AkropolisPlugin plugin,
            final @NotNull String name,
            final @NotNull List<String> aliases,
            final @NotNull List<String> actions
    ) {
        super(plugin, name, "A custom Akropolis command", aliases);
        this.actions = actions;
    }

    @Override
    protected void onCommand(CommandSender sender, String label, String[] args) {
        final Messages messages = this.getPlugin().getConfigManager().getFile(Messages.class).getConfig();

        if (!(sender instanceof Player player)) {
            MessagingUtil.send(messages.general().consoleNotAllowed(), sender);
            return;
        }

        if (permission != null && !sender.hasPermission(permission)) {
            MessagingUtil.send(messages.general().customCommandNoPermission(), sender);
            return;
        }

        AkropolisPlugin.getInstance().getActionManager().executeActions(player, actions);
    }

    public void setPermission(final @NotNull String permission) {
        this.permission = permission;
    }

    public @Nullable String getPermission() {
        return permission;
    }

    public @NotNull List<String> getActions() {
        return actions;
    }
}
