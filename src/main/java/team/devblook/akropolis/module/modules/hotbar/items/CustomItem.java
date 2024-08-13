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

package team.devblook.akropolis.module.modules.hotbar.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.module.modules.hotbar.HotbarItem;
import team.devblook.akropolis.module.modules.hotbar.HotbarManager;

import java.util.List;

public class CustomItem extends HotbarItem {
    private final List<String> actions;

    public CustomItem(HotbarManager hotbarManager, ItemStack item, int slot, String key) {
        super(hotbarManager, item, slot, key);
        actions = getPlugin().getConfigManager().getFile(ConfigType.SETTINGS).get()
                .getStringList("custom_join_items.items." + key + ".actions");
    }

    @Override
    protected void onInteract(Player player) {
        getPlugin().getActionManager().executeActions(player, actions);
    }
}
