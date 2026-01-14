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

package me.zetastormy.akropolis.module.modules.hotbar.items;

import java.util.Collections;
import java.util.List;

import me.zetastormy.akropolis.config.type.Messages;
import me.zetastormy.akropolis.config.type.Settings;
import me.zetastormy.akropolis.util.MessagingUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.zetastormy.akropolis.module.modules.hotbar.HotbarItem;
import me.zetastormy.akropolis.module.modules.hotbar.HotbarManager;
import net.kyori.adventure.text.Component;

public class CustomItem extends HotbarItem {
    private final String key;
    private final int cooldown;
    private final List<String> actions;

    public CustomItem(HotbarManager hotbarManager, ItemStack item, int slot, String key) {
        super(hotbarManager, item, slot, key);
        this.key = key;

        Settings.ItemRecord itemRecord = getPlugin().getConfigManager().getFile(Settings.class)
                .getConfig().customJoinItems().items().get(key);

        if (itemRecord == null) {
            cooldown = 0;
            actions = Collections.emptyList();
            return;
        }

        cooldown = itemRecord.cooldown();
        actions = itemRecord.actions();
    }

    @Override
    protected void onInteract(Player player) {
        final Messages messages = this.getPlugin().getConfigManager().getFile(Messages.class).getConfig();

        if (!getHotbarManager().tryCooldown(player.getUniqueId(), key, cooldown)) {
            MessagingUtil.sendWithReplacement(
                    messages.general().cooldownActive(),
                    player,
                    "time",
                    Component.text(getHotbarManager().getCooldown(player.getUniqueId(), key))
            );
            return;
        }

        getPlugin().getActionManager().executeActions(player, actions);
    }
}
