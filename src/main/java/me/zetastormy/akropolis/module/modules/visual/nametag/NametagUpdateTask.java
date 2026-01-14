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

package me.zetastormy.akropolis.module.modules.visual.nametag;

import me.zetastormy.akropolis.config.type.Settings;
import org.bukkit.Bukkit;

import me.zetastormy.akropolis.util.text.PlaceholderUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;

public class NametagUpdateTask implements Runnable {
    private final NametagHelper nametagHelper;
    private final String prefix;
    private final TextColor color;
    private final String suffix;

    public NametagUpdateTask(NametagManager nametagManager, NametagHelper nametagHelper) {
        this.nametagHelper = nametagHelper;

        Settings.Nametag.Format format = nametagManager.getConfig(Settings.class).nametag().format();

        this.prefix = format.prefix();
        this.color = TextColor.fromHexString(format.nameColor());
        this.suffix = format.suffix();
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            ScoreboardTeam team = nametagHelper.getMainTeamManager().team(player.getName());

            if (team == null) return;

            Component prefix = PlaceholderUtil.setPlaceholders(this.prefix, player);
            Component suffix = PlaceholderUtil.setPlaceholders(this.suffix, player);

            nametagHelper.updateFormat(team.defaultDisplay(), prefix, color, suffix);
        });
    }
}
