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

import com.cryptomorin.xseries.XSound;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

import java.util.Optional;

public class SoundAction implements Action {

    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        Optional<XSound> xsound = XSound.matchXSound(data);

        try {
            xsound.ifPresent(s -> {
                Sound sound = s.parseSound();

                if (sound == null) throw new IllegalStateException();

                player.playSound(player.getLocation(), sound, 1L, 1L);
            });
        } catch (Exception ex) {
            plugin.getLogger().warning("[Akropolis Action] Invalid sound name: " + data.toUpperCase());
        }
    }
}
