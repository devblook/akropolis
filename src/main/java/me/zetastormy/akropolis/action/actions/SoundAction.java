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

package me.zetastormy.akropolis.action.actions;

import java.util.Optional;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XSound;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.action.Action;

public class SoundAction implements Action {

    @Override
    public String getIdentifier() {
        return "SOUND";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        Optional<XSound> xsound = XSound.of(data);

        try {
            xsound.ifPresent(s -> {
                Sound sound = s.get();

                if (sound == null) throw new IllegalStateException();

                player.playSound(player.getLocation(), sound, 1L, 1L);
            });
        } catch (Exception ex) {
            plugin.getLogger().warning("[Akropolis Action] Invalid sound name: " + data.toUpperCase());
        }
    }
}
