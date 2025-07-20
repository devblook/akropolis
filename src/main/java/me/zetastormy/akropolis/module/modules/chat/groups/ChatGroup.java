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

import org.bukkit.entity.Player;

import me.zetastormy.akropolis.util.PlaceholderUtil;
import me.zetastormy.akropolis.util.TextUtil;
import net.kyori.adventure.text.Component;

public class ChatGroup {
    private final String rawFormat;
    private final int cooldownTime;
    private final String cooldownMessage;
    private final Emojis emojis;
    private final String permission;

    public ChatGroup(String groupName, String rawFormat, int cooldownTime, String cooldownMessage, Emojis emojis) {
        this.rawFormat = rawFormat;
        this.cooldownTime = cooldownTime;
        this.cooldownMessage = cooldownMessage;
        this.emojis = emojis;
        this.permission = "akropolis.chat.group." + groupName;
    }

    public Component getFormat(Player player) {
        return PlaceholderUtil.setPlaceholders(rawFormat, player);
    }

    public String parseEmojis(String message) {
        return emojis.parse(message);
    }

    public int getCooldownTime() {
        return cooldownTime;
    }

    public Component getCooldownMessage() {
        return TextUtil.parse(cooldownMessage);
    }

    public String getPermission() {
        return permission;
    }
}
