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

import com.cryptomorin.xseries.XPotion;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import team.devblook.akropolis.AkropolisPlugin;
import team.devblook.akropolis.action.Action;

import java.util.Optional;

public class PotionEffectAction implements Action {

    @Override
    public String getIdentifier() {
        return "EFFECT";
    }

    @Override
    public void execute(AkropolisPlugin plugin, Player player, String data) {
        String[] args = data.split(";");
        Optional<XPotion> xpotion = XPotion.matchXPotion(args[0]);

        try {
            xpotion.ifPresent(p -> {
                PotionEffect potionEffect = p.buildPotionEffect(1000000, Integer.parseInt(args[1]) - 1);

                if (potionEffect == null) throw new IllegalStateException();

                player.addPotionEffect(potionEffect);
            });
        } catch (Exception e) {
            plugin.getLogger().warning("[Akropolis Action] Invalid potion effect name: " + data.toUpperCase());
        }
    }
}
