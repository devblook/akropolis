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

package team.devblook.akropolis.action.actions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;
import team.devblook.akropolis.util.TextUtil;

import java.time.Duration;

public class TitleAction implements Action {

    @Override
    public String getIdentifier() {
        return "TITLE";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        String[] args = data.split(";");

        Component title = TextUtil.parse(args[0]);
        Component subTitle = TextUtil.parse(args[1]);

        Duration fadeIn;
        Duration stay;
        Duration fadeOut;

        try {
            fadeIn = Duration.ofSeconds(Long.parseLong(args[2]));
            stay = Duration.ofSeconds(Long.parseLong(args[3]));
            fadeOut = Duration.ofSeconds(Long.parseLong(args[4]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            fadeIn = Duration.ofSeconds(1);
            stay = Duration.ofSeconds(3);
            fadeOut = Duration.ofSeconds(1);
        }

        player.showTitle(Title.title(title, subTitle, Title.Times.times(fadeIn, stay, fadeOut)));
    }
}
