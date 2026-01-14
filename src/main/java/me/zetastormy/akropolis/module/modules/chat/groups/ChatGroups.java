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

package me.zetastormy.akropolis.module.modules.chat.groups;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import me.zetastormy.akropolis.config.type.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.LifeCycle;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import me.zetastormy.akropolis.util.text.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ChatGroups extends Module implements LifeCycle {
    private final Map<String, ChatGroup> chatGroups = new HashMap<>();

    public ChatGroups(AkropolisPlugin plugin) {
        super(plugin, ModuleType.CHAT_FORMAT);
    }

    @Override
    public void onEnable() {
        Settings config = getConfig(Settings.class);
        Settings.ChatManagement chatManagement = config.chatManagement();
        Map<String, Settings.ChatManagement.ChatGroup> groups = chatManagement.groups();

        groups.keySet().forEach(
                groupName -> {
                    Settings.ChatManagement.ChatGroup group = groups.get(groupName);
                    chatGroups.put(
                            groupName,
                            new ChatGroup(
                                    groupName,
                                    group.format(groupName),
                                    group.priority(),
                                    group.cooldown().time(),
                                    group.cooldown().message(),
                                    new Emojis(group.emojis())
                            )
                    );
                }
        );
    }

    @Override
    public void onDisable() {
        chatGroups.clear();
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Set<ChatGroup> playerGroups = new HashSet<>();
        ChatGroup topGroup = null;

        for (String group : chatGroups.keySet()) {
            if (player.hasPermission("akropolis.chat.group." + group)) {
                playerGroups.add(chatGroups.get(group));

                if (topGroup == null || chatGroups.get(group).getPriority() >= topGroup.getPriority()) {
                    topGroup = chatGroups.get(group);
                }
            }
        }

        if (topGroup == null) return;

        event.setCancelled(true);

        if (!tryCooldown(player.getUniqueId(), "chat", topGroup.getCooldownTime())) {
            player.sendMessage(TextUtil.replace(topGroup.getCooldownMessage(),
                "time",
                Component.text(getCooldown(player.getUniqueId(), "chat"))));

            return;
        }

        String rawMessage = TextUtil.raw(event.originalMessage());
        String parsedMessageEmojis = rawMessage;

        for (ChatGroup group : playerGroups) {
            parsedMessageEmojis = group.parseEmojis(parsedMessageEmojis);
        }

        String parsedMessage = TextUtil.raw(LegacyComponentSerializer
                .legacySection()
                .deserialize(ChatColor.translateAlternateColorCodes('&', parsedMessageEmojis)));

        getPlugin().getServer().sendMessage(TextUtil.replace(topGroup.getFormat(player),
                "message",
                TextUtil.parse(parsedMessage)));
    }
}
