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

package team.devblook.akropolis.module.modules.visual.nametag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import team.devblook.akropolis.config.ConfigType;
import team.devblook.akropolis.util.PlaceholderUtil;

public class NametagUpdateTask implements Runnable {
    private final NametagHelper nametagHelper;
    private final String prefix;
    private final TextColor color;
    private final String suffix;

    public NametagUpdateTask(NametagManager nametagManager, NametagHelper nametagHelper) {
        this.nametagHelper = nametagHelper;

        ConfigurationSection format = nametagManager.getConfig(ConfigType.SETTINGS).getConfigurationSection("nametag.format");

        this.prefix = format == null ? "" : format.getString("prefix");
        this.color = TextColor.fromHexString(format == null ? "#FFFFFF" : format.getString("name_color", "#FFFFFF"));
        this.suffix = format == null ? "" : format.getString("suffix");
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
