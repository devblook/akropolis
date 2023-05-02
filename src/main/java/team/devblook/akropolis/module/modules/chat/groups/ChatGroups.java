/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2023 DevBlook Team and others
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

package team.devblook.akropolis.module.modules.chat.groups;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.cooldown.CooldownType;
import team.devblook.akropolis.module.Module;
import team.devblook.akropolis.module.ModuleType;
import team.devblook.akropolis.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class ChatGroups extends Module {
    private final Map<String, ChatGroup> chatGroups = new HashMap<>();

    public ChatGroups(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_FORMAT);
    }

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig(ConfigType.SETTINGS);
        ConfigurationSection groups = config.getConfigurationSection("groups");

        if (groups == null) {
            throw new NullPointerException("No chat groups in configuration, please add the new section to you config.yml!");
        }

        groups.getKeys(false).forEach(groupName -> chatGroups.put(groupName, new ChatGroup(groupName,
                groups.getString(groupName + ".format", "No format."),
                groups.getInt(groupName + ".cooldown.time", 0),
                groups.getString(groupName + ".cooldown.message", "No cooldown message."))));
    }

    @Override
    public void onDisable() {
        chatGroups.clear();
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ChatGroup currentGroup = chatGroups.get("default");

        for (String group : chatGroups.keySet()) {
            if (player.hasPermission("akropolis.chat.group." + group)) {
                currentGroup = chatGroups.get(group);
            }
        }

        event.setCancelled(true);

        if (!tryCooldown(player.getUniqueId(), CooldownType.CHAT, currentGroup.getCooldownTime())) {
            player.sendMessage(TextUtil.replace(currentGroup.getCooldownMessage(), "time", Component.text(getCooldown(player.getUniqueId(), CooldownType.CHAT))));
            return;
        }

        getPlugin().getServer().sendMessage(TextUtil.replace(currentGroup.getFormat(player),
                "message",
                event.originalMessage()));
    }
}
