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

import org.bukkit.configuration.ConfigurationSection;

public class Emojis {
    private final Map<Set<String>, Set<String>> emojis;

    public Emojis(ConfigurationSection emojiSection) {
        if (emojiSection == null) {
            this.emojis = Map.of();
            return;
        }

        this.emojis = new HashMap<>();

        emojiSection.getKeys(false).forEach(key -> {
            Set<String> emoticon = new HashSet<>(emojiSection.getStringList("emoticon"));
            Set<String> emoji = new HashSet<>(emojiSection.getStringList("emoji"));
            this.emojis.put(emoticon, emoji);
        });
    }
}
