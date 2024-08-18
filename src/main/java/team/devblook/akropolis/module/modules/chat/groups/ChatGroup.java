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

package team.devblook.akropolis.module.modules.chat.groups;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.akropolis.util.PlaceholderUtil;
import team.devblook.akropolis.util.TextUtil;

public class ChatGroup {
    private final String rawFormat;
    private final int cooldownTime;
    private final String cooldownMessage;
    private final String permission;

    public ChatGroup(String groupName, String rawFormat, int cooldownTime, String cooldownMessage) {
        this.rawFormat = rawFormat;
        this.cooldownTime = cooldownTime;
        this.cooldownMessage = cooldownMessage;
        this.permission = "akropolis.chat.group." + groupName;
    }

    public Component getFormat(Player player) {
        return PlaceholderUtil.setPlaceholders(rawFormat, player);
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
