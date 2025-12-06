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

package me.zetastormy.akropolis.hook;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.hook.hooks.head.BaseHead;
import me.zetastormy.akropolis.hook.hooks.head.DatabaseHead;
import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import me.zetastormy.akropolis.util.text.TextUtil;

public class HooksManager {
    private final Map<String, PluginHook> hooks;

    public HooksManager(AkropolisPlugin plugin) {
        hooks = new HashMap<>();

        // Base64 head
        hooks.put("BASE64", new BaseHead());

        // PlaceholderAPI
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            hooks.put("PLACEHOLDER_API", null);
            PlaceholderUtil.setPapiState(true);
            plugin.getLogger().info("Hooked into PlaceholderAPI");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("HeadDatabase")) {
            hooks.put("HEAD_DATABASE", new DatabaseHead());
            plugin.getLogger().info("Hooked into HeadDatabase");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
            hooks.put("MINIPLACEHOLDERS", null);
            PlaceholderUtil.setMPState(true);
            TextUtil.setMPState(true);
            plugin.getLogger().info("Hooked into MiniPlaceholders");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            hooks.put("NOTEBLOCK_API", null);
            plugin.getLogger().info("Hooked into NoteBlockAPI");
        }

        hooks.values().stream().filter(Objects::nonNull).forEach(pluginHook -> pluginHook.onEnable(plugin));
    }

    public boolean isHookEnabled(String id) {
        return hooks.containsKey(id);
    }

    public PluginHook getPluginHook(String id) {
        return hooks.get(id);
    }
}
