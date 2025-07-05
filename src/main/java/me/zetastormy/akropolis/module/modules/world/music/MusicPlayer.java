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

package me.zetastormy.akropolis.module.modules.world.music;

import me.zetastormy.akropolis.AkropolisPlugin;
import me.zetastormy.akropolis.module.Module;
import me.zetastormy.akropolis.module.ModuleType;
import org.bukkit.Bukkit;

import java.io.File;

public class MusicPlayer extends Module {

    public MusicPlayer(AkropolisPlugin plugin) {
        super(plugin, ModuleType.MUSIC_PLAYER);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    private void loadSongs() {
        File directory = new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "songs");

        if (!directory.exists()) {
            if (!directory.mkdir()) {
                getPlugin().getLogger().severe("Could not create songs' directory!");
                getPlugin().getLogger().severe("The plugin will now disable.");
                Bukkit.getPluginManager().disablePlugin(getPlugin());
                return;
            }


        }
    }
}
