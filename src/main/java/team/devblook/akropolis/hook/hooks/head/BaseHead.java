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

package team.devblook.akropolis.hook.hooks.head;

import com.cryptomorin.xseries.XMaterial;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.hook.PluginHook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BaseHead implements PluginHook, HeadHook {
    private AkropolisPlugin plugin;
    private Map<String, ItemStack> cache;

    @Override
    public void onEnable(AkropolisPlugin plugin) {
        this.plugin = plugin;
        cache = new HashMap<>();
    }

    @Override
    public ItemStack getHead(String data) {
        if (cache.containsKey(data)) return cache.get(data);

        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();

        if (head == null) {
            plugin.getLogger().severe("Could not parse head!");
            return XMaterial.SKELETON_SKULL.parseItem();
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta == null) {
            plugin.getLogger().severe("Could not parse head meta!");
            return head;
        }
        head.editMeta(SkullMeta.class, skullMeta -> {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
            Collection<ProfileProperty> properties = profile.getProperties();
            properties.clear();
            properties.add(new ProfileProperty("textures", data, ""));
            profile.setProperties(properties);
            skullMeta.setPlayerProfile(profile);
        });
        cache.put(data, head);
        return head;
    }
}
