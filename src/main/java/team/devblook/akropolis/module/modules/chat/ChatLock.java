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

package team.devblook.akropolis.module.modules.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.Permissions;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.config.Message;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;

public class ChatLock extends Module {
    private boolean isChatLocked;

    public ChatLock(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_LOCK);
    }

    @Override
    public void onEnable() {
        isChatLocked = getPlugin().getConfigManager().getFile(ConfigType.DATA).get().getBoolean("chat_locked");
    }

    @Override
    public void onDisable() {
        getPlugin().getConfigManager().getFile(ConfigType.DATA).get().set("chat_locked", isChatLocked);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!isChatLocked || player.hasPermission(Permissions.LOCK_CHAT_BYPASS.getPermission()))
            return;

        event.setCancelled(true);
        Message.CHAT_LOCKED.sendFrom(player);
    }

    public boolean isChatLocked() {
        return isChatLocked;
    }

    public void setChatLocked(boolean chatLocked) {
        isChatLocked = chatLocked;
    }
}
