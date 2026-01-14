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
import org.bukkit.command.CommandSender;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.Permissions;
import me.zetastormy.akropolis.command.InjectableCommand;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.module.modules.chat.ChatLock;
import me.zetastormy.akropolis.util.text.TextUtil;

public class LockchatCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public LockchatCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "lockchat", "Locks global chat", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        final Messages messages = this.plugin.getConfigManager().getFile(Messages.class).getConfig();

        if (!sender.hasPermission(Permissions.COMMAND_LOCKCHAT.getPermission())) {
            MessagingUtil.send(messages.general().noPermission(), sender);
            return;
        }

        ChatLock chatLockModule = (ChatLock) plugin.getModuleManager().getModule(ModuleType.CHAT_LOCK);

        if (chatLockModule.isChatLocked()) {
            plugin.getServer().broadcast(TextUtil.replace(
                            MessagingUtil.toComponent(messages.chat().unlockedBroadcast()),
                            "player",
                            sender.name()
                    ));
            chatLockModule.setChatLocked(false);
        } else {
            plugin.getServer().broadcast(TextUtil.replace(
                    MessagingUtil.toComponent(messages.chat().lockedBroadcast()),
                    "player",
                    sender.name()
            ));
            chatLockModule.setChatLocked(true);
        }

    }
}
