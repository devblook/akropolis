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

package me.zetastormy.akropolis.module.modules.visual.scoreboard;

import java.util.List;

import org.bukkit.entity.Player;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;

public class ScoreboardHelper {
    private final Sidebar sidebar;
    private final Player player;

    public ScoreboardHelper(Player player) {
        this.player = player;
        this.sidebar = AkropolisPlugin.getInstance().getScoreboardLibrary().createSidebar(Sidebar.MAX_LINES);
    }

    public void setTitle(String title) {
        sidebar.title(setPlaceholders(title));
    }

    public void setLinesFromList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            sidebar.line(i, setPlaceholders(list.get(i)));
        }
    }

    public Component setPlaceholders(String text) {
        return PlaceholderUtil.setPlaceholders(text, player);
    }

    public void addPlayer() {
        sidebar.addPlayer(player);
    }

    public void removePlayer() {
        if (sidebar.closed()) return;

        sidebar.removePlayer(player);
        sidebar.close(); // To prevent memory leaks
    }
}
