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
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.command.InjectableCommand;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.module.modules.chat.ChatLock;
import team.devblook.akropolis.util.TextUtil;

import java.util.List;

public class LockchatCommand extends InjectableCommand {
    private final AkropolisPlugin plugin;

    public LockchatCommand(AkropolisPlugin plugin, List<String> aliases) {
        super(plugin, "lockchat", "Locks global chat", aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        if (!sender.hasPermission(Permissions.COMMAND_LOCKCHAT.getPermission())) {
            Message.NO_PERMISSION.sendFrom(sender);
            return;
        }

        ChatLock chatLockModule = (ChatLock) plugin.getModuleManager().getModule(ModuleType.CHAT_LOCK);

        if (chatLockModule.isChatLocked()) {
            plugin.getServer().broadcast(
                    TextUtil.replace(Message.CHAT_UNLOCKED_BROADCAST.toComponent(), "player", sender.name()));
            chatLockModule.setChatLocked(false);
        } else {
            plugin.getServer()
                    .broadcast(
                            TextUtil.replace(Message.CHAT_LOCKED_BROADCAST.toComponent(), "player", sender.name()));
            chatLockModule.setChatLocked(true);
        }

    }
}
