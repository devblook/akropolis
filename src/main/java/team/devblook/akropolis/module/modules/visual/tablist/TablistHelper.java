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

package team.devblook.akropolis.module.modules.visual.tablist;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import team.devblook.akropolis.util.TextUtil;

import java.util.Objects;

public class TablistHelper {

    private TablistHelper() {
        throw new UnsupportedOperationException();
    }

    public static void sendTabList(Player player, Component header, Component footer) {
        Objects.requireNonNull(player, "Cannot update tab for null player");
        Component newHeader = header == null ? Component.empty() : TextUtil.replace(header, "player", player.displayName());
        Component newFooter = footer == null ? Component.empty() : TextUtil.replace(footer, "player", player.displayName());

        player.sendPlayerListHeaderAndFooter(newHeader, newFooter);
    }
}
