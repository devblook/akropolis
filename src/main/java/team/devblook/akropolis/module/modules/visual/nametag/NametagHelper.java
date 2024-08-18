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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;

import java.util.HashMap;
import java.util.Map;

public class NametagHelper {
    private final TeamManager mainTeamManager;
    private final Map<Player, ScoreboardTeam> teams;

    public NametagHelper() {
        this.mainTeamManager = AkropolisPlugin.getInstance().getScoreboardLibrary().createTeamManager();
        this.teams = new HashMap<>();
    }

    public void createFormat(Component prefix, TextColor color, Component suffix, Player player) {
        if (teams.containsKey(player)) {
            ScoreboardTeam team = teams.get(player);
            TeamDisplay teamDisplay = team.defaultDisplay();

            team.teamManager().addPlayer(player);
            updateFormat(teamDisplay, prefix, color, suffix);
            teamDisplay.addEntry(player.getName());
            return;
        }

        ScoreboardTeam team = mainTeamManager.createIfAbsent(player.getName());
        TeamDisplay teamDisplay = team.defaultDisplay();

        team.teamManager().addPlayer(player);
        updateFormat(teamDisplay, prefix, color, suffix);
        teamDisplay.addEntry(player.getName());
        teams.put(player, team);
    }

    public void updateFormat(TeamDisplay teamDisplay, Component prefix, TextColor color, Component suffix) {
        teamDisplay.prefix(prefix);
        teamDisplay.playerColor(NamedTextColor.nearestTo(color));
        teamDisplay.suffix(suffix);
    }

    public void deleteFormat(Player player) {
        if (!teams.containsKey(player) || mainTeamManager.closed()) return;

        ScoreboardTeam team = teams.get(player);

        if (team != null) {
            team.teamManager().removePlayer(player);
            team.defaultDisplay().removeEntry(player.getName());
        }
    }

    public TeamManager getMainTeamManager() {
        return mainTeamManager;
    }
}
